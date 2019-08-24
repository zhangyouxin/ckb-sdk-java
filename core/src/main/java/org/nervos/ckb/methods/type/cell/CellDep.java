package org.nervos.ckb.methods.type.cell;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.nervos.ckb.methods.type.OutPoint;

/** Copyright © 2019 Nervos Foundation. All rights reserved. */
public class CellDep {

  public static final String CODE = "code";
  public static final String DEP_GROUP = "dep_group";

  @JsonProperty("out_point")
  public OutPoint outPoint;

  @JsonProperty("dep_type")
  public String depGroup;

  public CellDep() {}

  public CellDep(OutPoint outPoint, String depGroup) {
    this.outPoint = outPoint;
    this.depGroup = depGroup;
  }
}
