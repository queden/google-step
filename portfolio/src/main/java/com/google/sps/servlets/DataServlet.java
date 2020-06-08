// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList; 
import com.google.sps.data.Comment;

/** Servlet that creates and retrieves comments on website */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  /**
   * Retrieves all comments from Datastore and displays on website
   *
   * @param request GET request
   * @param response GET response
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String maxCommentsString = request.getParameter("max-comments");

    // Sets maxComments to a default value for if user selects choice of all
    // (which is only possible string value to select). Otherwise, sets maxComments to the int value
    // of the string
    int maxComments = -1;
    if (!maxCommentsString.equals("all")) {
      try {
        maxComments = Integer.parseInt(maxCommentsString);
      } catch (Exception e) {
        System.out.println("Error parsing selection");
      }
    }

    // Queries datastore for all comments
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    ArrayList<Comment> comments = new ArrayList<Comment>();
    for (Entity entity : results.asIterable()) {
        // adds to comments list if "all" choice was chosen or if less than amount of requested comments
        if (maxComments == -1 || comments.size() < maxComments) {
            String name = (String) entity.getProperty("name");
            Double mood = (Double) entity.getProperty("mood");
            String comment = (String) entity.getProperty("comment");
            long timestamp = (long) entity.getProperty("timestamp");

            Comment com = new Comment(name, mood, comment, timestamp);
            comments.add(com);
        }
    }

    response.setContentType("application/json;");
    String json = new Gson().toJson(comments);
    response.getWriter().println(json);
  }

  /**
   * Posts a comment to Datastore submitted by the user
   *
   * @param request POST request
   * @param response POST response
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("user");
    double mood = Double.parseDouble(request.getParameter("mood"));
    String comment = request.getParameter("comment");
    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("mood", mood);
    commentEntity.setProperty("comment", comment);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/#connect");
  }
}
