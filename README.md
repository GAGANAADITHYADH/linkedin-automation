# LinkedIn UI Test Automation (IT3153 Software Quality Assurance)

Automated UI tests for LinkedIn written with Java, Selenium WebDriver and TestNG, using the Page Object Model.

## Tests
| Class | Test | What it checks |
|---|---|---|
| LoginTest | validLogin | valid credentials reach the feed and the search is visible |
| LoginTest | invalidPassword | a wrong password shows an error message |
| SearchTest | searchShowsResults | searching shows a results page with a visible result |
| OpenResultTest | openFirstResult | the first result opens the matching page |

## Requirements
- Java 17 or later (developed with Java 27)
- Maven 3.9 or later
- Google Chrome (the driver is downloaded automatically by Selenium Manager)

## Setup
1. Clone the repository.
2. Copy `config.properties.example` to `config.properties` and fill in your own test account:
   ```
   linkedin.username=your-email@example.com
   linkedin.password=your-password
   ```
   Alternatively set the environment variables `LINKEDIN_USERNAME` and `LINKEDIN_PASSWORD`.
   `config.properties` is listed in `.gitignore` and must never be committed.

## Run
```
mvn clean test
```
Run one class only: `mvn clean test -Dtest=LoginTest`

## Notes
- Use only your own test account. LinkedIn may show a CAPTCHA or security checkpoint. The tests do not bypass it:
  complete the step manually in the browser, the test waits up to 3 minutes.
- A screenshot is saved to `screenshots/` automatically when a test fails (TestNG listener).
- LinkedIn changes its page structure often, so locators may need updating.

## Project structure
- `src/test/java/pages/` page objects (LoginPage, SearchPage)
- `src/test/java/base/` BaseTest and the screenshot listener
- `src/test/java/utils/` config reader and screenshot helpers
- `src/test/java/tests/` the test classes
- `testng.xml` suite definition
