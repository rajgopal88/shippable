package models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Model file for getting the essential part from the json returned from the
 */
@Entity
public class Issue {

  @Id
  private int id;

  private String state;

  @JsonProperty("created_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private Date createdAt;

  @JsonProperty("pull_request")
  private PullRequest pullRequest;

  public int getId() { return id; }

  public void setId(int id) { this.id = id; }

  public String getState() { return state; }

  public void setState(String state) { this.state = state; }

  public Date getCreatedAt() { return createdAt; }

  public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

  public PullRequest getPullRequest() { return pullRequest; }

  public void setPullRequest(PullRequest pullRequest) { this.pullRequest = pullRequest; }
}
