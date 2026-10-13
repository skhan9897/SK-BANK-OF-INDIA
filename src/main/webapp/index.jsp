<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>SK Bank Of India - Loading...</title>
    <style>
        body {
            background: linear-gradient(135deg, #0b2545 0%, #134074 100%);
            color: white;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            overflow: hidden;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .splash-container {
            text-align: center;
            animation: fadeIn 1s ease-in-out;
        }
        .logo-circle {
            width: 150px;
            height: 150px;
            background: radial-gradient(circle, #1d4ed8 0%, #0b2545 100%);
            border: 4px solid #f4d03f;
            border-radius: 50%;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            margin: 0 auto 1.5rem auto;
            box-shadow: 0 0 30px rgba(244, 208, 63, 0.5);
            animation: pulse 2s infinite;
        }
        .logo-circle h2 {
            margin: 0;
            color: #f4d03f;
            font-size: 2.5rem;
            font-weight: bold;
        }
        .logo-circle span {
            font-size: 0.7rem;
            color: #ffffff;
            letter-spacing: 1.5px;
            font-weight: 600;
        }
        h1 {
            color: #f4d03f;
            font-size: 2.2rem;
            margin-bottom: 0.5rem;
            text-shadow: 0 2px 4px rgba(0,0,0,0.3);
        }
        p {
            color: #cbd5e1;
            font-size: 1.1rem;
            margin-bottom: 2.5rem;
        }
        .progress-bar-container {
            width: 300px;
            height: 8px;
            background: rgba(255, 255, 255, 0.2);
            border-radius: 4px;
            margin: 0 auto;
            overflow: hidden;
        }
        .progress-bar {
            width: 0%;
            height: 100%;
            background: #f4d03f;
            border-radius: 4px;
            animation: loadProgress 2.8s linear forwards;
        }
        @keyframes loadProgress {
            0% { width: 0%; }
            100% { width: 100%; }
        }
        @keyframes pulse {
            0% { transform: scale(1); box-shadow: 0 0 20px rgba(244, 208, 63, 0.4); }
            50% { transform: scale(1.06); box-shadow: 0 0 35px rgba(244, 208, 63, 0.8); }
            100% { transform: scale(1); box-shadow: 0 0 20px rgba(244, 208, 63, 0.4); }
        }
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(15px); }
            to { opacity: 1; transform: translateY(0); }
        }
    </style>
    <script>
        setTimeout(function() {
            window.location.href = "${pageContext.request.contextPath}/home";
        }, 2800); // 2.8 seconds splash time
    </script>
</head>
<body>
    <div class="splash-container">
        <div class="logo-circle">
            <h2>SK</h2>
            <span>BANK OF INDIA</span>
        </div>
        <h1>SK Bank Of India</h1>
        <p>A Stronger India Through Secure Banking</p>
        <div class="progress-bar-container">
            <div class="progress-bar"></div>
        </div>
        <p style="font-size: 0.85rem; margin-top: 1.2rem; color: #94a3b8;">Loading Secure Banking Environment...</p>
    </div>
</body>
</html>
