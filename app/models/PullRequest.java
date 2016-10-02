package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by raj on 2/10/16.
 */
@Entity
public class PullRequest extends Model {

  @Id
  private int id;
  private String url;
  private String html_url;
  private String diff_url;
  private String patch_url;
}
