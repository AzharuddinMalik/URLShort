// ===== GLOBAL VARIABLES =====
const urlHistory = [];

// ===== DOM CONTENT LOADED =====
document.addEventListener('DOMContentLoaded', () => {
    initThemeToggle();
    initUrlShortener();
    loadHistory();
    initScrollReveal(); // Initialize scroll reveal
    initSparkleEffect(); // Initialize sparkle effect for CTA
});

// ===== THEME TOGGLE =====
function initThemeToggle() {
    const toggle = document.getElementById('theme-toggle');
    const html = document.documentElement;

    // Check for saved theme preference
    const savedTheme = localStorage.getItem('theme') || 'light';
    html.setAttribute('data-theme', savedTheme);
    updateThemeIcon(savedTheme);

    toggle.addEventListener('click', () => {
        const currentTheme = html.getAttribute('data-theme');
        const newTheme = currentTheme === 'light' ? 'dark' : 'light';
        html.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        updateThemeIcon(newTheme);
    });
}

function updateThemeIcon(theme) {
    const toggle = document.getElementById('theme-toggle');
    const icon = toggle.querySelector('i');
    // Ensure the icon updates correctly based on the new design context's primary colors
    icon.className = theme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
}

// ===== URL SHORTENER =====
function initUrlShortener() {
    const shortenBtn = document.getElementById('shorten-btn');
    const urlInput = document.getElementById('longUrl');

    shortenBtn.addEventListener('click', shortenUrl);

    urlInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            shortenUrl();
        }
    });
}

async function shortenUrl() {
    const longUrl = document.getElementById('longUrl').value.trim();
    const resultDiv = document.getElementById('result');
    const loader = document.getElementById('loader');
    const btnText = document.querySelector('.btn-text');
    const shortenBtn = document.getElementById('shorten-btn');
    const customPath = document.getElementById('customPath').value.trim() || '';
    const selectedDomain = document.getElementById('domainSelect').value; // Get the selected domain

    // Clear previous results
    resultDiv.classList.add('hidden');
    resultDiv.classList.remove('error');

    // Validation (keep your existing validation logic)
    if (!longUrl) {
        showResult('Please enter a URL', true);
        return;
    }

    if (!isValidUrl(longUrl)) {
        showResult('Please enter a valid URL (must start with http:// or https://)', true);
        return;
    }

    // Add back frontend custom path validation if you removed it
    if (customPath && !/^[a-zA-Z0-9_-]{3,20}$/.test(customPath)) {
        showResult('Custom path must be 3-20 characters long and contain only letters, numbers, hyphens, or underscores.', true);
        return;
    }
    if (customPath && (customPath.toLowerCase().startsWith('api/url') || customPath.toLowerCase().startsWith('css') || customPath.toLowerCase().startsWith('js'))) {
        showResult('Custom path cannot start with "api/url", "css", or "js". Please choose a different path.', true);
        return;
    }
    // End Validation

    try {
        // Show loading state
        loader.classList.remove('hidden');
        btnText.textContent = 'Shortening...';
        shortenBtn.disabled = true;

        // Build request params (This part is correct)
        const params = new URLSearchParams();
        params.append('longUrl', longUrl);
        if (customPath) {
            params.append('customPath', customPath);
        }

        const res = await fetch('/api/url/shorten', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: params.toString()
        });

        // Refined error handling
        if (!res.ok) {
            const errorData = await res.json(); // Assuming backend sends JSON error
            throw new Error(errorData.message || res.statusText);
        }

        const data = await res.json();
        // data.shortUrl from backend will be like "/api/url/ABCDEFGHI" or "/api/url/my-custom-alias"
        const backendShortUrlPath = data.shortUrl; // This is the path your backend can resolve

        // FOR DISPLAY: Create a "pretty" short URL using the selected domain
        const displayShortCode = backendShortUrlPath.replace('/api/url/', '');
        const displayFullShortUrl = `https://${selectedDomain}/${displayShortCode}`;

        // FOR REDIRECTION: The actual URL the browser will use for redirection,
        // which must point to your local backend's redirect endpoint.
        const clickableFullShortUrl = `${window.location.origin}${backendShortUrlPath}`;


        showResult(`
            <div class="success-text">✅ Short URL created!</div>
            <p><strong>Short URL:</strong> <a href="${clickableFullShortUrl}" class="short-url-link" target="_blank">${displayFullShortUrl}</a></p>
            <p><strong>Original:</strong> <a href="${longUrl}" class="short-url-link" target="_blank">${truncateUrl(longUrl, 60)}</a></p>
        `, false);

        // Add to history with the URL that ACTUALLY works for redirection, and the display URL
        addToHistory(longUrl, clickableFullShortUrl, displayFullShortUrl);

        document.getElementById('longUrl').value = '';
        document.getElementById('customPath').value = '';
    } catch (err) {
        showResult(`❌ Error: ${err.message}`, true);
    } finally {
        loader.classList.add('hidden');
        btnText.textContent = 'Shorten URL';
        shortenBtn.disabled = false;
    }
}

function isValidUrl(string) {
    try {
        new URL(string);
        return string.startsWith('http://') || string.startsWith('https://');
    } catch (_) {
        return false;
    }
}

function showResult(message, isError = false) {
    const resultDiv = document.getElementById('result');
    resultDiv.innerHTML = message;
    resultDiv.classList.toggle('error', isError);
    resultDiv.classList.remove('hidden');
}

function truncateUrl(url, maxLength) {
    return url.length > maxLength ? url.substring(0, maxLength) + '...' : url;
}

// ===== HISTORY MANAGEMENT =====
function addToHistory(longUrl, clickableShortUrl, displayShortUrl) {
    const historyItem = {
        longUrl,
        shortUrl: clickableShortUrl, // Store the URL that actually works for redirection
        displayUrl: displayShortUrl, // Store the pretty URL for display
        timestamp: new Date().toISOString(),
        id: Date.now()
    };
    urlHistory.unshift(historyItem);
    if (urlHistory.length > 10) { urlHistory.pop(); }
    try { localStorage.setItem('urlHistory', JSON.stringify(urlHistory)); } catch (e) { console.warn('Unable to save to localStorage:', e); }
    loadHistory();
}

function loadHistory() {
    const historyList = document.getElementById('historyList');
    try {
        const saved = localStorage.getItem('urlHistory');
        if (saved) {
            const parsedHistory = JSON.parse(saved);
            urlHistory.splice(0, urlHistory.length, ...parsedHistory);
        }
    } catch (e) { console.warn('Unable to load from localStorage:', e); }

    if (urlHistory.length === 0) {
        historyList.innerHTML = '<p style="text-align: center; padding: 2rem; color: var(--secondary);">No URLs shortened yet</p>';
        return;
    }

    historyList.innerHTML = urlHistory.map(item => `
            <div class="history-item">
                <div>
                    <div>
                        <a href="${item.shortUrl}" class="short-url-link" target="_blank">${truncateUrl(item.displayUrl || item.shortUrl, 35)}</a>
                    </div>
                    <div class="timestamp">${formatDate(item.timestamp)}</div>
                </div>
                <div>
                    <a href="${item.longUrl}" class="short-url-link" target="_blank">View Original</a>
                </div>
            </div>
        `).join('');
}

function formatDate(isoString) {
    const date = new Date(isoString);
    return date.toLocaleString('en-US', {
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// ===== SMOOTH SCROLLING =====
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// ===== SCROLL REVEAL ANIMATION =====
function initScrollReveal() {
    const revealElements = document.querySelectorAll('.scroll-reveal');

    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('is-visible');
                observer.unobserve(entry.target); // Unobserve once visible
            }
        });
    }, {
        threshold: 0.1, // Element is 10% visible
        rootMargin: '0px 0px -50px 0px' // Start animation a bit before it enters the viewport fully
    });

    revealElements.forEach(element => {
        observer.observe(element);
    });
}

// ===== CTA SPARKLE EFFECT =====
function initSparkleEffect() {
    const sparkleContainer = document.querySelector('.sparkle-effect');
    if (!sparkleContainer) return;

    function createSparkle() {
        const sparkle = document.createElement('div');
        sparkle.classList.add('sparkle');
        sparkleContainer.appendChild(sparkle);

        const size = Math.random() * 5 + 5; // Sparkle size between 5px and 10px
        const x = Math.random() * 100; // Random X position (percentage)
        const y = Math.random() * 100; // Random Y position (percentage)
        const duration = Math.random() * 1 + 0.5; // Animation duration

        sparkle.style.width = `${size}px`;
        sparkle.style.height = `${size}px`;
        sparkle.style.left = `${x}%`;
        sparkle.style.top = `${y}%`;
        sparkle.style.animationDuration = `${duration}s`;

        sparkle.addEventListener('animationend', () => {
            sparkle.remove();
        });
    }

    // Create sparkles periodically
    setInterval(createSparkle, 200); // Adjust interval for more/less sparkles
}


document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const user = localStorage.getItem('username');

// Update navbar
    if (token) {
        document.getElementById('nav-auth').style.display = 'none';
        document.getElementById('logout-btn').style.display = 'inline-block';
        document.getElementById('nav-user').textContent = `Welcome, ${user}`;
    }

    document.getElementById('logout-btn')?.addEventListener('click', () => {
        // localStorage.clear();
        window.location.href = '/login.html';
    });

// Sign-up handler
    document.getElementById('signup-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const body = {
            username: document.getElementById('signup-username').value,
            email: document.getElementById('signup-email').value,
            password: document.getElementById('signup-password').value
        };
        const res = await fetch('/api/auth/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        if (res.ok) {
            alert('Sign-up successful! Please log in.');
            window.location.href = '/login.html';
        } else {
            alert('Sign-up failed.');
        }
    });

// Login handler
    document.getElementById('login-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const body = {
            email: document.getElementById('login-email').value,
            password: document.getElementById('login-password').value
        };
        const res = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        if (res.ok) {
            const data = await res.json();
            localStorage.setItem('token', data.token);
            localStorage.setItem('username', data.username);
            window.location.href = '/index.html';
        } else {
            alert('Login failed.');
        }
    });
});
