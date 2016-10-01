package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Shippable;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.html.master;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletionStage;


/**
 * Created by raj on 28/9/16.
 */
public class ShippableController extends Controller {

  //String final_link = "https://api.github.com/repos/Shippable/support/issues?page=1&per_page=100";
  @Inject
  WSClient ws;

  public Result shippableUI() {
    return ok(master.render());
  }
  public CompletionStage<Result> shippableLink() throws MalformedURLException {
    Http.RequestBody body = request().body();
    JsonNode json = body.asJson();
    String link = json.findPath("link").asText();
    URL url = new URL(link);
    String path = url.getPath();
    String finalLink = "https://api.github.com/repos";
    String pageLink = "?page=1&per_page=100";
    finalLink=finalLink.concat(path);
    finalLink=finalLink.concat(pageLink);
    List<Shippable> shippableList = null;
    return ws.url(finalLink).get()
        .thenApply(WSResponse::asJson)
        .thenApply(wsr -> {
              return shippableList.fromJson(wsr, Shippable.class);
            }
        );
  }
}