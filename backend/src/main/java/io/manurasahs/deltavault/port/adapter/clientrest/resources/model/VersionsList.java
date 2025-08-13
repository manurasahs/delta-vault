package io.manurasahs.deltavault.port.adapter.clientrest.resources.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.manurasahs.deltavault.port.adapter.clientrest.resources.model.VersionInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * VersionsList
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class VersionsList {

  private String fileName;

  @Valid
  private List<@Valid VersionInfo> versions = new ArrayList<>();

  public VersionsList() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public VersionsList(String fileName, List<@Valid VersionInfo> versions) {
    this.fileName = fileName;
    this.versions = versions;
  }

  public VersionsList fileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  /**
   * Get fileName
   * @return fileName
   */
  @NotNull 
  @JsonProperty("fileName")
  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public VersionsList versions(List<@Valid VersionInfo> versions) {
    this.versions = versions;
    return this;
  }

  public VersionsList addVersionsItem(VersionInfo versionsItem) {
    if (this.versions == null) {
      this.versions = new ArrayList<>();
    }
    this.versions.add(versionsItem);
    return this;
  }

  /**
   * Get versions
   * @return versions
   */
  @NotNull @Valid 
  @JsonProperty("versions")
  public List<@Valid VersionInfo> getVersions() {
    return versions;
  }

  public void setVersions(List<@Valid VersionInfo> versions) {
    this.versions = versions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VersionsList versionsList = (VersionsList) o;
    return Objects.equals(this.fileName, versionsList.fileName) &&
        Objects.equals(this.versions, versionsList.versions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileName, versions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VersionsList {\n");
    sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
    sb.append("    versions: ").append(toIndentedString(versions)).append("\n");
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

