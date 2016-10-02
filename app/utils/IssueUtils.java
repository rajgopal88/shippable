package utils;

/**
 * Created by raj on 2/10/16.
 */
public class IssueUtils {

  public static String getApi(int page, int noOfIssue, String repoPath) {
    String finalLink = "https://api.github.com/repos";
    String pageLink = "?page="+ page+"&per_page="+noOfIssue;
    finalLink=finalLink.concat(repoPath);
    finalLink=finalLink.concat(pageLink);
    return finalLink;
  }
}
