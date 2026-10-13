<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>SK Bank Of India - A Stronger India Through Secure Banking</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .hero-section {
            background: linear-gradient(135deg, #0b2545 0%, #134074 100%);
            color: white;
            text-align: center;
            padding: 4rem 2rem;
            border-radius: 8px;
            margin-bottom: 2rem;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        .hero-section h1 {
            font-size: 2.5rem;
            margin-bottom: 1rem;
            color: #f4d03f;
        }
        .hero-section p {
            font-size: 1.2rem;
            margin-bottom: 2rem;
            color: #dcdcdc;
        }
        .cta-buttons a {
            display: inline-block;
            background-color: #f4d03f;
            color: #0b2545;
            padding: 0.75rem 2rem;
            border-radius: 4px;
            font-weight: bold;
            text-decoration: none;
            margin: 0.5rem;
            box-shadow: 0 2px 4px rgba(0,0,0,0.2);
            transition: background 0.3s;
        }
        .cta-buttons a:hover {
            background-color: #d4ac0d;
        }
        .cta-buttons a.secondary {
            background-color: transparent;
            color: white;
            border: 2px solid #f4d03f;
        }
        .cta-buttons a.secondary:hover {
            background-color: rgba(244, 208, 63, 0.1);
        }
        .features-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 1.5rem;
            margin-top: 2rem;
        }
        .feature-card {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
            text-align: center;
        }
        .feature-card h3 {
            color: #0b2545;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <header>
        <div style="display: flex; align-items: center; gap: 1rem;">
            <span style="font-size: 1.8rem; font-weight: bold; color: #f4d03f;">🇮🇳 SK Bank Of India</span>
        </div>
        <nav>
            <a href="${pageContext.request.contextPath}/home">Home</a>
            <a href="${pageContext.request.contextPath}/ifsc-search">IFSC & Branch Search</a>
            <a href="${pageContext.request.contextPath}/login">Login</a>
            <a href="${pageContext.request.contextPath}/register">Open Account</a>
        </nav>
    </header>

    <div class="container">
        <div class="hero-section">
            <h1>A Stronger India Through Secure Banking</h1>
            <p>TRUST • SECURITY • PROSPERITY</p>
            <div class="cta-buttons">
                <a href="register">Open Savings Account</a>
                <a href="ifsc-search" class="secondary">Search IFSC Code</a>
                <a href="login" class="secondary">Customer Login</a>
            </div>
        </div>

        <div class="features-grid">
            <div class="feature-card">
                <h3>🏛️ Multi-Bank & IFSC Directory</h3>
                <p>Search official verified IFSC codes across public, private, and regional banks in India with real-time verification status.</p>
            </div>
            <div class="feature-card">
                <h3>🔒 Secure Double-Entry Ledger</h3>
                <p>Advanced transaction and ledger engine built with BigDecimal precision, row-level locking, and idempotency guarantees.</p>
            </div>
            <div class="feature-card">
                <h3>⚡ Fast NEFT, RTGS & UPI</h3>
                <p>Seamless internal and external fund transfers with complete audit trails and instant passbook statements.</p>
            </div>
        </div>
    </div>
</body>
</html>
