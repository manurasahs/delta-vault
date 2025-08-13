package io.manurasahs.deltavault.port.adapter.clientrest.resources.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SimpleInfoMessage
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class SimpleInfoMessage {

  private String info;

  public SimpleInfoMessage() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SimpleInfoMessage(String info) {
    this.info = info;
  }

  public SimpleInfoMessage info(String info) {
    this.info = info;
    return this;
  }

  /**
   * Simple information message.
   * @return info
   */
  @NotNull 
  @JsonProperty("info")
  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimpleInfoMessage simpleInfoMessage = (SimpleInfoMessage) o;
    return Objects.equals(this.info, simpleInfoMessage.info);
  }

  @Override
  public int hashCode() {
    return Objects.hash(info);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SimpleInfoMessage {\n");
    sb.append("    info: ").append(toIndentedString(info)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

