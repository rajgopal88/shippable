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

  String final_link = "https://api.github.com/repos/Shippable/support/issues?page=1&per_page=100&state=open";

  @Inject
  WSClient ws;

  //function to render the UI
  public Result issueUI() { return ok(master.render()); }

  /**
   *
   * @return
   * @throws MalformedURLException
   */
  public CompletionStage<Result> issueLink() throws MalformedURLException {
    Http.RequestBody body = request().body();
    JsonNode json = body.asJson();
    String link = json.findPath("link").asText();
    URL url = new URL(link);
    String path = url.getPath();

    String finalLink = IssueUtils.getApi(1, 100, path);
    List<Issue> issueList = null;

    return getIssues(1, 100, path).thenApply(issues -> ok(Json.toJson(issueDetails(issues))));
  }
    /*return ws.url(finalLink).get()
        .thenApply(WSResponse::getBody)
        .thenApply(wsr -> {
          try {
            List<Issue> issue = Json.mapper().readValue(wsr,
                Json.mapper().getTypeFactory().constructCollectionType(List.class, Issue.class));
            return ok(Json.toJson(issueDetails(issue)));
          } catch (IOException e) {
            e.printStackTrace();
            }
            return ok("ok");
          });
  }*/

  private CompletionStage<List<Issue>> getIssues(int page, int noIssues, String path) {
    String finalLink = IssueUtils.getApi(page, noIssues, path);
    System.out.println(finalLink);
    CompletionStage<List<Issue>> issuesCompletionStage = ws.url(finalLink).get()
        .thenApply(WSResponse::getBody)
        .thenApply(wsr -> {
          try {
            List<Issue> issues = Json.mapper().readValue(wsr,
                Json.mapper().getTypeFactory().constructCollectionType(List.class, Issue.class));
            return issues;
          } catch (IOException e) {
            e.printStackTrace();
          }
          return new ArrayList<Issue>();
        });
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

  public Map<String,Integer> issueDetails(List<Issue> sh) {
    int i = sh.size();
    int lt24h = 0;
    int gt24lt7d = 0;
    int gt7d = 0;
    for(Issue s:sh) {
      String createdDate = s.getCreated_at();
      //String updatedDate = s.getUpdated_at();

      DateTime now = DateTime.now();

      DateTime isoDate = new DateTime(createdDate, DateTimeZone.UTC);
      DateTimeFormatter dateTimeFormatter = DateTimeFormat
          .forPattern("MM/dd/yyyy HH:mm:ss")
          .withZone(DateTimeZone.forID("Asia/Kolkata"));
      String newCreatedDate = dateTimeFormatter.print(isoDate);

      DateTime isoDate1 = new DateTime(now, DateTimeZone.UTC);
      DateTimeFormatter dateTimeFormatter1 = DateTimeFormat
          .forPattern("MM/dd/yyyy HH:mm:ss")
          .withZone(DateTimeZone.forID("Asia/Kolkata"));
      String newUpdatedDate = dateTimeFormatter1.print(isoDate1);

      SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      Date d1 = null;
      Date d2 = null;
      try {
        d1 = format.parse(newCreatedDate);
        d2 = format.parse(newUpdatedDate);
        //in milliseconds
        long diff = d2.getTime() - d1.getTime();
        //long diffHours = diff / (60 * 60 * 1000) % 24;
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
    //int[] issueDetails = new int[4];
    Map<String,Integer> issueDetails = new HashMap<>();
    issueDetails.put("totalIssue",i);
    issueDetails.put("lessThan24Hours",lt24h);
    issueDetails.put("greaterThan24HoursLessThan7Days",gt24lt7d);
    issueDetails.put("greaterThan7Days",gt7d);
    return issueDetails;
  }
}