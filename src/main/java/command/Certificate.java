package command;

import org.bson.Document;

public class Certificate {
    public Document _id;
    public String organization;
    public String datestart;
    public String dateend;
    public String status;
    public String comment;

    @Override
    public String toString() {
        if (comment.length() > 1) {
            return organization + ", " + datestart + ", " + dateend + ", " + status + ", " + comment;
        } else {
            return organization + ", " + datestart + ", " + dateend + ", " + status + ", " + "-";
        }
    }
}
