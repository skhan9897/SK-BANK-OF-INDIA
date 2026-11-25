<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SK Bank of India - Initializing Digital Portal</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome Icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #0A192F;
            color: #FFFFFF;
            font-family: 'Inter', system-ui, -apple-system, sans-serif;
            height: 100vh;
            margin: 0;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            align-items: center;
            padding: 40px 20px;
            overflow: hidden;
            position: relative;
        }

        /* Indian Tricolor Glowing Backdrop Aura */
        .tricolor-glow {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 320px;
            height: 320px;
            background: radial-gradient(circle, rgba(255,153,51,0.15) 0%, rgba(255,255,255,0.1) 50%, rgba(19,136,8,0.15) 100%);
            border-radius: 50%;
            filter: blur(40px);
            z-index: 1;
        }

        .splash-content {
            position: relative;
            z-index: 2;
            text-align: center;
            margin: auto;
        }

        .bank-logo-icon {
            font-size: 4.5rem;
            color: #D4AF37;
            text-shadow: 0 0 25px rgba(212, 175, 55, 0.4);
            animation: pulseLogo 1.5s ease-in-out infinite alternate;
        }

        @keyframes pulseLogo {
            0% { transform: scale(1); }
            100% { transform: scale(1.08); }
        }

        .india-flag-badge {
            background: rgba(255, 255, 255, 0.08);
            border: 1px solid rgba(255, 255, 255, 0.15);
            backdrop-filter: blur(10px);
            padding: 8px 20px;
            border-radius: 30px;
            display: inline-flex;
            align-items: center;
            gap: 10px;
            font-size: 0.9rem;
            letter-spacing: 0.5px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }

        /* Progress Bar */
        .splash-progress-container {
            width: 240px;
            height: 4px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 10px;
            overflow: hidden;
            margin: 25px auto 0;
        }

        .splash-progress-bar {
            width: 0%;
            height: 100%;
            background: linear-gradient(90deg, #FF9933 0%, #FFFFFF 50%, #138808 100%);
            border-radius: 10px;
            animation: fillProgress 3s cubic-bezier(0.1, 0.5, 0.5, 1) forwards;
        }

        @keyframes fillProgress {
            0% { width: 0%; }
            100% { width: 100%; }
        }

        /* Bottom Credits Transition */
        .footer-credits {
            position: relative;
            z-index: 2;
            text-align: center;
            animation: fadeInUp 1s ease-out;
        }

        .credit-name {
            font-size: 1.1rem;
            font-weight: 700;
            color: #D4AF37;
            letter-spacing: 1px;
            text-transform: uppercase;
        }

        @keyframes fadeInUp {
            0% { opacity: 0; transform: translateY(15px); }
            100% { opacity: 1; transform: translateY(0); }
        }
    </style>
</head>
<body>

<div class="tricolor-glow"></div>

<div class="splash-content">
    <div class="bank-logo-icon mb-3">
        <i class="fa-solid fa-building-columns"></i>
    </div>
    <h1 class="fw-bold text-white mb-2" style="font-size: 2.5rem; letter-spacing: 1px;">
        SK Bank <span style="color: #D4AF37;">of India</span>
    </h1>

    <div class="india-flag-badge mb-3">
        <span style="font-size: 1.2rem;">🇮🇳</span>
        <span class="text-white fw-semibold">PROUDLY LAUNCHED IN INDIA</span>
    </div>

    <p class="text-white-50 small mb-0">Next-Generation Digital Banking Platform</p>

    <!-- 3-Second Loading Bar -->
    <div class="splash-progress-container">
        <div class="splash-progress-bar"></div>
    </div>
</div>

<!-- Bottom Credits Section -->
<div class="footer-credits">
    <div class="small text-white-50 mb-1">DEVELOPED BY</div>
    <div class="credit-name mb-1"><i class="fa-solid fa-code me-2"></i>SAJID KHAN</div>
    <div class="small text-white-50"><i class="fa-solid fa-shield-halved text-success me-1"></i> RBI Regulated Digital Banking System • 2025</div>
</div>

<script>
    // Exact 3-Second (3000ms) Auto Transition to Home Dashboard
    setTimeout(function() {
        window.location.href = '${pageContext.request.contextPath}/index.jsp';
    }, 3000);
</script>

</body>
</html>
