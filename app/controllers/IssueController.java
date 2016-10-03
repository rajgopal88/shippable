package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Issue;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletionStage;


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
  public Map<String,Integer> issueDetails(List<Issue> issues) {
    int totalIssue = 0;
    int lt24h = 0;
    int gt24lt7d = 0;
    int gt7d = 0;
    for(Issue issue:issues) {
      //filtering the pull request as the github api returns both the issue and pull request as issues
      if(issue.getPullRequest() == null) {
        totalIssue++;

        Date isoDate = issue.getCreatedAt();
        DateTime now = DateTime.now();

        DateTime isoDate1 = new DateTime(now, DateTimeZone.UTC);
        DateTimeFormatter dateTimeFormatter1 = DateTimeFormat
            .forPattern("MM/dd/yyyy HH:mm:ss")
            .withZone(DateTimeZone.forID("Asia/Kolkata"));
        
        String newUpdatedDate = dateTimeFormatter1.print(isoDate1);

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date d1 = null;
        Date d2 = null;
        try {
          d1 = isoDate;
          d2 = format.parse(newUpdatedDate);

          //in milliseconds
          long diff = d2.getTime() - d1.getTime();
          long diffDays = diff / (24 * 60 * 60 * 1000);

          if(diffDays == 0) {
            lt24h+=1;
          } else if(diffDays > 0 && diffDays<7) {
            gt24lt7d+=1;
          } else if(diffDays>7) {
            gt7d+=1;
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    Map<String,Integer> issueDetails = new HashMap<>();
    issueDetails.put("totalIssue",totalIssue);
    issueDetails.put("lessThan24Hours",lt24h);
    issueDetails.put("greaterThan24HoursLessThan7Days",gt24lt7d);
    issueDetails.put("greaterThan7Days",gt7d);
    return issueDetails;
  }
}