package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;

/**
 * Created by raj on 2/10/16.
 */
@Entity
public class PullRequest {

  private String url;

  @JsonProperty("html_url")
  private String htmlUrl;

  @JsonProperty("diff_url")
  private String diffUrl;

  @JsonProperty("patch_url")
  private String patchUrl;

  public String getUrl() { return url; }

  public void setUrl(String url) { this.url = url; }

  public String getHtmlUrl() { return htmlUrl; }

  public void setHtmlUrl(String htmlUrl) { this.htmlUrl = htmlUrl; }

  public String getDiffUrl() { return diffUrl; }

  public void setDiffUrl(String diffUrl) { this.diffUrl = diffUrl; }

  public String getPatchUrl() { return patchUrl; }

  public void setPatchUrl(String patchUrl) { this.patchUrl = patchUrl; }
}
