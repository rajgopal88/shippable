package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Issue;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.IssueUtils;
import views.html.html.master;

import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * Created by raj on 28/9/16.
 */
public class IssueController extends Controller {

  @Inject
  WSClient ws;

  /**
   * function to render the UI
   * @return
   */
  public Result issueUI() { return ok(master.render()); }

  /**
   * api function which takes github repository link as input through POST
   * and sends the issue details as output
   * @return
   * @throws MalformedURLException
   */
  public CompletionStage<Result> issueLink() throws MalformedURLException {

    //POST Body
    Http.RequestBody body = request().body();
    JsonNode json = body.asJson();
    String link = json.findPath("link").asText();

    //Parsing the user entered link
    URL url = new URL(link);

    //getting the url path
    String path = url.getPath();

    //getting the github api link
    String finalLink = IssueUtils.getApi(1, 100, path);

    List<Issue> issueList = null;

    //calling the function which processes the api link and sends the issue details
    return getIssues(1, 100, path).thenApply(issues -> ok(Json.toJson(issueDetails(issues))));
  }

  private CompletionStage<List<Issue>> getIssues(int page, int noIssues, String path) {

    /**
     * getting the github api link again for recursive call
     */
    String finalLink = IssueUtils.getApi(page, noIssues, path);

    /**
     * Asynchronous call which call teh webservice and gets all the issue details
     */
    CompletionStage<List<Issue>> issuesCompletionStage = ws.url(finalLink).get()
        .thenApply(WSResponse::getBody)
        .thenApply(wsr -> {
          try {

            //converting all the issue in modal format excluding all other details
            List<Issue> issues = Json.mapper().readValue(wsr,
                Json.mapper().getTypeFactory().constructCollectionType(List.class, Issue.class));
            return issues;
          } catch (IOException e) {
            e.printStackTrace();
          }
          return new ArrayList<Issue>();
        });

    /**
     * receursive call to this function because the api generated can read a
     * maximum of 100 issue at a time so this is done to read all the other issues.
     */
    CompletionStage<List<Issue>> uCompletionStage = issuesCompletionStage.thenComposeAsync(issues1 -> {

      if (issues1.size() < noIssues) return issuesCompletionStage;
      else {
        CompletionStage<List<Issue>> wCompletionStage = getIssues(page + 1, noIssues, path)
            .thenApply(issues2 -> {
              issues1.addAll(issues2);
              return issues1;
            });
        return wCompletionStage;
      }

    });

    return uCompletionStage;
  }

  /**
   * function which receives list of processed issue and processes this
   * data to get the issue details and returns a map containing all the details.
   */
  private Map<String,Integer> issueDetails(List<Issue> allIssues) {
    int totalIssue = 0;
    int lt24h = 0;
    int gt24lt7d = 0;
    int gt7d = 0;

    //filtering the issue and removing the pull requests
    List<Issue> issues = allIssues.stream().filter(this::isIssue).collect(Collectors.toList());

    if(issues != null) {
      //total no. of open issues
      totalIssue = issues.size();

      //total no. of issues created in past 24 hours
      lt24h = issues.stream().filter(this::isLt24hIssue).collect(Collectors.toList()).size();

      //total no. of issues created >24 hours but less than 7 days
      gt24lt7d = issues.stream().filter(this::isGt24Lt7dIssue).collect(Collectors.toList()).size();

      //total no. of issues created greater than 7 days
      gt7d = issues.stream().filter(this::isGt7dIssue).collect(Collectors.toList()).size();
    }

    Map<String,Integer> issueDetails = new HashMap<>();
    issueDetails.put("totalIssue",totalIssue);
    issueDetails.put("lessThan24Hours",lt24h);
    issueDetails.put("greaterThan24HoursLessThan7Days",gt24lt7d);
    issueDetails.put("greaterThan7Days",gt7d);
    return issueDetails;
  }

  /**
   * Checking if the issue is only an issue or a pull request beccause the api return all the pull request as issues
   * @param issue
   * @return
   */
  private boolean isIssue(Issue issue) {
     return (issue.getPullRequest() == null) ? true:false;
  }

  /**
   * Check if the issue was created in less than 24 hours
   * @param issue which need to be checked
   * @return
   */
  private boolean isLt24hIssue(Issue issue) {
    Date issueDate = issue.getCreatedAt();
    Date now = new Date();
    long timeDiff = now.getTime() - issueDate.getTime();

    return TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS) < 1;
  }

  /**
   * Check if the issue was created in greater than 24 hours but less than 7 days
   * @param issue which need to be checked
   * @return
   */
  private boolean isGt24Lt7dIssue(Issue issue) {
    Date issueDate = issue.getCreatedAt();
    Date now = new Date();
    long timeDiff = now.getTime() - issueDate.getTime();

    return (TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS) >= 1 &&
        TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS) < 7) ? true:false;
  }

  /**
   * Check if the issue was created in greater than 7 days
   * @param issue which need to be checked
   * @return
   */
  private boolean isGt7dIssue(Issue issue) {
    Date issueDate = issue.getCreatedAt();
    Date now = new Date();
    long timeDiff = now.getTime() - issueDate.getTime();

    return (TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS) >= 7) ? true:false;
  }

}