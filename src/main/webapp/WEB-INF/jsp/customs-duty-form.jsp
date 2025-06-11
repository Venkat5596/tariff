<!DOCTYPE html>
<html>
<head>
  <title>Add/Edit Customs Duty</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    h1 { color: #333; }
    a { color: #007bff; text-decoration: none; }
    a:hover { text-decoration: underline; }
    .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
    form div { margin-bottom: 10px; }
    label { display: block; margin-bottom: 5px; font-weight: bold; }
    input[type="text"], input[type="datetime-local"] {
      width: calc(100% - 22px);
      padding: 10px;
      border: 1px solid #ccc;
      border-radius: 4px;
      font-size: 1em;
    }
    button {
      padding: 10px 20px;
      background-color: #28a745;
      color: white;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      font-size: 1em;
    }
    button:hover { background-color: #218838; }
    .message { color: green; font-weight: bold; }
    .error { color: red; font-weight: bold; }
  </style>
</head>
<body>
<div class="container">
  <h1>Add New Customs Duty</h1>
  <p><a href="/customs-duties">Back to List</a></p>

  <%-- Display flash messages --%>
  <% if (request.getAttribute("message") != null) { %>
  <p class="message">${requestScope.message}</p>
  <% } %>
  <% if (request.getAttribute("error") != null) { %>
  <p class="error">${requestScope.error}</p>
  <% } %>

  <form action="/customs-duties/save" method="post">
    <div>
      <label for="tariffNumber">Tariff Number:</label>
      <input type="text" id="tariffNumber" name="tariffNumber" required>
    </div>
    <div>
      <label for="customsDutyCode">Customs Duty Code:</label>
      <input type="text" id="customsDutyCode" name="customsDutyCode" required>
    </div>
    <div>
      <label for="asOfDateString">As Of Date (YYYY-MM-DDTHH:MM:SS):</label>
      <input type="datetime-local" id="asOfDateString" name="asOfDateString" step="1" required>
    </div>
    <div>
      <label for="title">Title:</label>
      <input type="text" id="title" name="title">
    </div>
    <%-- Assuming 'updated' is set by system or fetched from external API only --%>
    <button type="submit">Save Customs Duty</button>
  </form>
</div>
</body>
</html>