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
import com.google.appengine.api.datastore.Blob;
import java.io.IOException;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList; 
import com.google.sps.data.CommentProtos.Comment;
import com.google.sps.data.CommentProtos.Comments;

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

    // Builds comments protobuf and adds all stored comment protobufs to it
    Comments.Builder commentsBuilder = Comments.newBuilder();
    int commentCount = 0;
    for (Entity entity : results.asIterable()) {
        // adds to comments list if "all" choice was chosen or if less than amount of requested comments
        if (maxComments == -1 || commentCount < maxComments) {
            // Retrieve Blob, convert to byte array, and parse back to Comment proto
            Comment commentProto = Comment.parseFrom(((Blob) entity.getProperty("proto")).getBytes());

            commentsBuilder.addComments(commentProto);

            commentCount++;
        }
    }

    Comments comments = commentsBuilder.build();

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
    String message = request.getParameter("comment");
    long timestamp = System.currentTimeMillis();

    Comment.Builder commentProto = Comment.newBuilder();

    commentProto.setName(name);
    commentProto.setMood(mood);
    commentProto.setMessage(message);
    commentProto.setTimestamp(timestamp);

    Comment commentBuilt = commentProto.build();
    // Serialize comment in a Blob for storing in Datastore
    Blob commentBlob = new Blob(commentBuilt.toByteArray());

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("proto", commentBlob);
    // Timestamp stored seperately for sorting on retrieval
    commentEntity.setProperty("timestamp", timestamp); 


    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/#connect");
  }
}