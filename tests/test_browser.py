import re
import threading
import unittest
from functools import partial
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path

from playwright.sync_api import (
    Browser,
    BrowserContext,
    Page,
    Playwright,
    expect,
    sync_playwright,
)

BUILD = Path(__file__).resolve().parents[1] / "build"


class BrowserTest(unittest.TestCase):
    server: ThreadingHTTPServer
    thread: threading.Thread
    playwright: Playwright
    browser: Browser
    context: BrowserContext
    page: Page

    @classmethod
    def setUpClass(cls) -> None:
        if not (BUILD / "site/index.html").is_file():
            raise RuntimeError("Build the site before running browser tests.")
        # Serve from a subdirectory to match a GitHub project Pages URL.
        cls.server = ThreadingHTTPServer(
            ("127.0.0.1", 0), partial(SimpleHTTPRequestHandler, directory=BUILD)
        )
        cls.thread = threading.Thread(target=cls.server.serve_forever, daemon=True)
        cls.thread.start()
        cls.addClassCleanup(cls.server.server_close)
        cls.addClassCleanup(cls.thread.join, 5)
        cls.addClassCleanup(cls.server.shutdown)
        cls.playwright = sync_playwright().start()
        cls.addClassCleanup(cls.playwright.stop)
        cls.browser = cls.playwright.chromium.launch()
        cls.addClassCleanup(cls.browser.close)

    def setUp(self) -> None:
        self.context = self.browser.new_context()
        self.addCleanup(self.context.close)
        self.page = self.context.new_page()
        self.base_url = f"http://127.0.0.1:{self.server.server_port}/site/"

    def test_runs_python_and_changes_views_and_integrator(self) -> None:
        errors: list[str] = []
        external_requests: list[str] = []
        self.page.on("pageerror", lambda error: errors.append(str(error)))
        self.context.on(
            "request",
            lambda request: (
                external_requests.append(request.url)
                if not request.url.startswith(self.base_url)
                else None
            ),
        )
        self.page.goto(self.base_url)
        start = self.page.get_by_role("button", name="Start", exact=True)
        expect(start).to_be_enabled(timeout=45_000)
        elapsed = self.page.locator("#elapsed")
        extent = self.page.locator("#extent")
        expect(elapsed).to_have_text("0.000 yr")
        expect(self.page.locator("#energy")).to_have_text(
            re.compile(r"^-\d\.\d+e\+\d+$")
        )
        expect(self.page.locator("#visible")).to_have_text("1,986")

        sky = self.page.locator("#sky")
        initial = sky.evaluate("(canvas) => canvas.toDataURL()")
        start.click()
        expect(elapsed).not_to_have_text("0.000 yr")
        self.page.get_by_role("button", name="Pause").click()
        expect(self.page.get_by_role("status")).to_have_text("Paused.")
        self.assertNotEqual(sky.evaluate("(canvas) => canvas.toDataURL()"), initial)

        self.page.get_by_role("button", name="Reset").click()
        expect(start).to_be_enabled()
        expect(elapsed).to_have_text("0.000 yr")
        self.assertEqual(sky.evaluate("(canvas) => canvas.toDataURL()"), initial)
        expect(extent).to_have_text("32.0 AU")
        self.page.get_by_role("button", name="Zoom in").click()
        expect(extent).to_have_text("16.0 AU")
        self.page.get_by_label("Center on").select_option(label="Earth")
        expect(self.page.get_by_label("Center on")).to_have_value("3")
        self.page.get_by_label("Show initial positions").check()
        expect(self.page.get_by_label("Show initial positions")).to_be_checked()

        self.page.get_by_label("Scenario").select_option("disk")
        expect(start).to_be_enabled()
        expect(self.page.get_by_label("Inclination (°)")).to_have_value("70")
        expect(self.page.locator("#visible")).to_have_text("998")
        self.page.get_by_label("Integrator").select_option("hermite")
        expect(start).to_be_enabled()
        expect(self.page.get_by_label("Accrete (Leapfrog)")).to_be_disabled()
        self.page.get_by_label("Show initial positions").uncheck()
        self.page.get_by_label("Show trails").check()
        start.click()
        expect(elapsed).not_to_have_text("0.000 yr")
        self.page.get_by_role("button", name="Pause").click()
        self.assertEqual(errors, [])
        self.assertEqual(external_requests, [])

        self.page.set_viewport_size({"width": 390, "height": 844})
        self.assertTrue(
            self.page.evaluate("document.documentElement.scrollWidth <= innerWidth")
        )
        expect(sky).to_be_visible()

    def test_runtime_load_failure_is_visible(self) -> None:
        self.page.route("**/vendor/pyodide.js", lambda route: route.abort())
        self.page.goto(self.base_url)
        expect(self.page.get_by_role("alert")).to_contain_text("Python could not load")
        expect(self.page.get_by_role("button", name="Reload simulator")).to_be_visible()
        expect(self.page.get_by_role("button", name="Start")).to_be_disabled()
