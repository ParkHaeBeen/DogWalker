package com.project.dogwalker.common.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.locationtech.jts.geom.Coordinate;

public class CoordinateDeserializer extends StdDeserializer<Coordinate> {

  public CoordinateDeserializer(Class <?> vc) {
    super(vc);
  }

  @Override
  public Coordinate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);
    double x = node.get("x").asDouble();
    double y = node.get("y").asDouble();
    return new Coordinate(x, y);
  }
}
