package com.project.dogwalker.common.service.route;

import com.project.dogwalker.common.service.route.dto.Location;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;

@Service
public class RouteService {

  private final String prefix = "LINESTRING(";

  public LineString CoordinateToLineString(final List <Coordinate> routes){
    GeometryFactory geometryFactory=new GeometryFactory();
    return geometryFactory.createLineString(routes.toArray(new Coordinate[0]));
  }

  public List<Location> LineStringToLocation(final String lineString){
    final List<Location> coordinates = new ArrayList <>();

    final String[] points = lineString.replace(prefix,"").replace(")","")
        .trim().split(",");
    for (String point : points) {
      final String[] parts = point.trim().split("\\s");
      coordinates.add(new Location(Double.parseDouble(parts[0]), Double.parseDouble(parts[1])));
    }

    return coordinates;
  }
}
