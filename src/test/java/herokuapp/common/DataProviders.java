package herokuapp.common;

import org.testng.annotations.DataProvider;

/**
 * Data Providers, for Herokuapp
 */
public class DataProviders {
    private static final String USER_DATA_DIR = "./src/test/resources/user_test_data/";

    /**
     * Data provider for user register
     * @return
     */
    @DataProvider(name = "covid19UserRegister")
    private static Object[][] covid19UserDataProvider() {
        try {
            return SpreadsheetUtils.getCsvTestDataIgnoringFirstRow(USER_DATA_DIR + "Covid19UserContent.csv");
        } catch (Throwable e) {
            return new Object[][] {{}};
        }
    }
}
