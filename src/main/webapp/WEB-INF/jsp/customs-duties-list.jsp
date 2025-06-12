<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
  <title>Customs Duties</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    h1 { color: #333; }
    a { color: #007bff; text-decoration: none; }
    a:hover { text-decoration: underline; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f2f2f2; }
    .container { max-width: 1000px; margin: 0 auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
    .button {
      display: inline-block;
      padding: 8px 12px;
      margin: 5px 0;
      background-color: #007bff;
      color: white;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      text-align: center;
      text-decoration: none;
      font-size: 0.9em;
    }
    .button.delete { background-color: #dc3545; }
    .button:hover { background-color: #0056b3; }
    .button.delete:hover { background-color: #c82333; }
    .message { color: green; font-weight: bold; }
    .error { color: red; font-weight: bold; }
    .actions { white-space: nowrap; }
  </style>
</head>
<body>
<div class="container">
  <h1>Customs Duties</h1>
  <p><a href="/">Back to Home</a></p>

  <%-- Display flash messages from redirects --%>
  <% if (request.getAttribute("message") != null) { %>
  <p class="message">${requestScope.message}</p>
  <% } %>
  <% if (request.getAttribute("error") != null) { %>
  <p class="error">${requestScope.error}</p>
  <% } %>

  <p><a href="${pageContext.request.contextPath}/customs-duties/create" class="button">Add New Customs Duty</a></p>
  <form action="${pageContext.request.contextPath}/customs-duties/fetch-external" method="post" style="display: inline-block; margin-left: 10px;">
    <button type="submit" class="button">Fetch & Store from External API</button>
  </form>


  <c:if test="${empty duties}">
    <p>No customs duties found in the database. Fetch from external API or add manually.</p>
  </c:if>
  <c:if test="${not empty duties}">
    <table>
      <thead>
      <tr>
        <th>Tariff Number</th>
        <th>Customs Duty Code</th>
        <th>As Of Date</th>
        <th>Title</th>
        <th>Updated Date</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="duty" items="${duties}">
        <tr>
          <td>${duty.id.tariffItemNumber}</td>
          <td>${duty.id.tariffTreatmentCode}</td>
          <td><fmt:formatDate value="${duty.asOfDateForDisplay}" pattern="yyyy-MM-dd HH:mm:ss"/></td>

          <td>
<%--            customsDuties(--%>
<%--            TariffItemNumber='${duty.id.tariffItemNumber}',--%>
<%--            TariffTreatmentCode='${duty.id.tariffTreatmentCode}',--%>
<%--            AsOfDate=datetime'<fmt:formatDate value="${duty.asOfDateForDisplay}" pattern="yyyy-MM-dd'T'HH:mm:ss" />')--%>

            <table style="margin-top:10px; border:1px solid #ccc; font-size: 0.9em;">
              <tr><th>Breakdown</th><th>Value</th></tr>
              <tr><td>Tariff Item</td><td>${duty.id.tariffItemNumber}</td></tr>
              <tr><td>Duty Code</td><td>${duty.id.tariffTreatmentCode}</td></tr>
              <tr><td>As of</td>
                <td><fmt:formatDate value="${duty.asOfDateForDisplay}" pattern="yyyy-MM-dd'T'HH:mm:ss" /></td>
              </tr>
            </table>
          </td>
          <td><fmt:formatDate value="${duty.updated}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
          <td class="actions">
              <%-- Edit Link (You'd need a /edit/{id} endpoint and form) --%>
              <%-- <a href="/customs-duties/edit/${duty.id.tariffNumber}/${duty.id.customsDutyCode}/${duty.id.asOfDate}" class="button">Edit</a> --%>
            <form action="${pageContext.request.contextPath}/customs-duties/delete" method="post" style="display:inline;">
              <input type="hidden" name="tariffItemNumber" value="${duty.id.tariffItemNumber}">
              <input type="hidden" name="tariffTreatmentCode" value="${duty.id.tariffTreatmentCode}">
              <input type="hidden" name="asOfDate" value="${duty.id.asOfDate}">
              <button type="submit" class="button delete" onclick="return confirm('Are you sure you want to delete this item?');">Delete</button>
            </form>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </c:if>
</div>
</body>
</html>