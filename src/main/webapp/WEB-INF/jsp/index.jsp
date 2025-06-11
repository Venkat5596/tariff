<!DOCTYPE html>
<html>
<head>
    <title>Customs Duty Manager</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #333; }
        a { color: #007bff; text-decoration: none; }
        a:hover { text-decoration: underline; }
        .container { max-width: 800px; margin: 0 auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        .button {
            display: inline-block;
            padding: 10px 15px;
            margin: 10px 0;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-align: center;
            text-decoration: none;
        }
        .button:hover { background-color: #218838; }
        .message { color: green; font-weight: bold; }
        .error { color: red; font-weight: bold; }
    </style>
</head>
<body>
<div class="container">
    <h1>Welcome to Customs Duty Manager</h1>
    <p>This application manages customs duty data.</p>

    <h2>Actions:</h2>
    <ul>
        <li><a href="/customs-duties">View All Customs Duties</a></li>
        <li>
            <form action="/customs-duties/fetch-external" method="post">
                <button type="submit" class="button">Fetch and Store from External API</button>
            </form>
        </li>
        <li><a href="/customs-duties/create">Add New Customs Duty (Manual)</a></li>
    </ul>

    <%-- Display flash messages from redirects --%>
    <% if (request.getAttribute("message") != null) { %>
    <p class="message">${requestScope.message}</p>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
    <p class="error">${requestScope.error}</p>
    <% } %>
</div>
</body>
</html>