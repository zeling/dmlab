/**
 * Created by zeling on 16/5/14.
 *
 * @author zeling
 */
public class PropositionParserTest {

    PropositionParser parser = new PropositionParser();

    public static void main(String[] args) {
        new PropositionParserTest().testParse();
    }

    public void testParse() {

        String[] success = {
                "(a \\and \t (\\not B))",
                "A",
                "A_{1}",
                "B_{2 }",
                "ab ",
                "bc_{ 1}",
                "(a \\imply b)",
                "(\\not a)",
                "(a \\and \t (\\not B))",
                "((\\not a) \n \\or HELLO) "
        };

        String[] failure = {
                "A _{1}",
                "A_{}",
                "A_ {1}",
                "((a \\and b)",
                "(a \\and )",
                "(a \\and b))",
                "((\\not b))",
                "(A)"
        };

        for (String suc : success) {
            if (!shouldSuccess(suc)) {
                System.out.println("Test failed, sth should pass didn't pass");
                return;
            }
        }

        for (String fail : failure) {
            if (!shouldFail(fail)) {
                System.out.println("Test Failed, sth shouldn't pass did pass");
                System.out.println(fail);
                return;
            }
        }

        System.out.println("Test passed");
    }


    private boolean shouldSuccess(String s) {
        try {
            parser.parse(s);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }
    private boolean shouldFail(String s) {
        try {
            parser.parse(s);
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return true;
        }
    }

}