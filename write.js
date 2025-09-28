// 카카오 SDK 초기화
if (typeof Kakao !== 'undefined' && Kakao.isInitialized && !Kakao.isInitialized()) {
    Kakao.init('YOUR_KAKAO_APP_KEY');
}

// 전역 변수
let currentUser = null;
let selectedWine = null;
let currentRating = 0;

// 샘플 와인 데이터 (실제로는 API에서 가져와야 함)
const wineDatabase = [
    { id: 1, name: '샤토 마고 2015', type: 'Red Wine', region: 'Bordeaux, France', producer: 'Château Margaux' },
    { id: 2, name: '돔 페리뇽 2012', type: 'Champagne', region: 'Champagne, France', producer: 'Moët & Chandon' },
    { id: 3, name: '카이무스 까베르네 소비뇽 2019', type: 'Red Wine', region: 'Napa Valley, USA', producer: 'Caymus Vineyards' },
    { id: 4, name: '클라우디 베이 소비뇽 블랑 2021', type: 'White Wine', region: 'Marlborough, New Zealand', producer: 'Cloudy Bay' },
    { id: 5, name: '바롤로 브루나테 2017', type: 'Red Wine', region: 'Piedmont, Italy', producer: 'Giuseppe Rinaldi' },
    { id: 6, name: '샤블리 그랑 크뤼 2020', type: 'White Wine', region: 'Burgundy, France', producer: 'Louis Michel' },
    { id: 7, name: '리오하 그란 레세르바 2015', type: 'Red Wine', region: 'Rioja, Spain', producer: 'Marqués de Riscal' },
    { id: 8, name: '피노 그리지오 2022', type: 'White Wine', region: 'Veneto, Italy', producer: 'Santa Margherita' }
];

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    //checkLoginStatus();
    initializeSliders();
    initializeStarRating();
    initializeWineSearch();
    initializeForm();
});

// 로그인 상태 확인
function checkLoginStatus() {
    const savedUser = localStorage.getItem('wine_diary_user');
    if (!savedUser) {
        alert('로그인이 필요합니다.');
        window.location.href = 'index.html';
        return;
    }

    currentUser = JSON.parse(savedUser);
    document.getElementById('user-name').textContent = `${currentUser.name}님`;
}

// 슬라이더 초기화
function initializeSliders() {
    const sliders = ['body', 'tannin', 'acidity', 'sweetness'];

    sliders.forEach(sliderId => {
        const slider = document.getElementById(sliderId);
        const valueDisplay = document.getElementById(`${sliderId}-value`);

        slider.addEventListener('input', function() {
            valueDisplay.textContent = this.value;
        });
    });
}

// 별점 시스템 초기화
function initializeStarRating() {
    const stars = document.querySelectorAll('.star');
    const ratingText = document.getElementById('rating-text');

    stars.forEach(star => {
        star.addEventListener('click', function() {
            const rating = parseInt(this.getAttribute('data-rating'));
            setRating(rating);
        });

        star.addEventListener('mouseover', function() {
            const rating = parseInt(this.getAttribute('data-rating'));
            highlightStars(rating);
        });
    });

    document.getElementById('star-rating').addEventListener('mouseleave', function() {
        highlightStars(currentRating);
    });
}

// 별점 설정
function setRating(rating) {
    currentRating = rating;
    highlightStars(rating);

    const ratingTexts = ['', '별로예요', '괜찮아요', '좋아요', '매우 좋아요', '최고예요'];
    document.getElementById('rating-text').textContent = ratingTexts[rating];
}

// 별점 하이라이트
function highlightStars(rating) {
    const stars = document.querySelectorAll('.star');
    stars.forEach((star, index) => {
        if (index < rating) {
            star.classList.add('active');
        } else {
            star.classList.remove('active');
        }
    });
}

// 와인 검색 초기화
function initializeWineSearch() {
    const searchInput = document.getElementById('wine-search');
    const resultsContainer = document.getElementById('wine-results');

    searchInput.addEventListener('input', function() {
        const query = this.value.trim();
        if (query.length > 0) {
            searchWines(query);
        } else {
            resultsContainer.innerHTML = '';
        }
    });
}

// 와인 검색
function searchWines(query) {
    const results = wineDatabase.filter(wine =>
        wine.name.toLowerCase().includes(query.toLowerCase()) ||
        wine.producer.toLowerCase().includes(query.toLowerCase()) ||
        wine.region.toLowerCase().includes(query.toLowerCase())
    );

    displayWineResults(results);
}

// 검색 결과 표시
function displayWineResults(wines) {
    const resultsContainer = document.getElementById('wine-results');

    if (wines.length === 0) {
        resultsContainer.innerHTML = '<div class="no-results">검색 결과가 없습니다.</div>';
        return;
    }

    const resultsHTML = wines.map(wine => `
        <div class="wine-result-item" onclick="selectWine(${wine.id})">
            <div class="wine-name">${wine.name}</div>
            <div class="wine-details">${wine.type} - ${wine.region}</div>
            <div class="wine-producer">${wine.producer}</div>
        </div>
    `).join('');

    resultsContainer.innerHTML = resultsHTML;
}

// 와인 선택
function selectWine(wineId) {
    selectedWine = wineDatabase.find(wine => wine.id === wineId);

    if (selectedWine) {
        document.getElementById('selected-wine-name').textContent = selectedWine.name;
        document.getElementById('selected-wine-details').textContent =
            `${selectedWine.type} - ${selectedWine.region} - ${selectedWine.producer}`;

        document.getElementById('wine-search').style.display = 'none';
        document.getElementById('wine-results').innerHTML = '';
        document.getElementById('selected-wine').style.display = 'block';
    }
}

// 와인 선택 해제
function clearWineSelection() {
    selectedWine = null;
    document.getElementById('wine-search').style.display = 'block';
    document.getElementById('wine-search').value = '';
    document.getElementById('selected-wine').style.display = 'none';
}

// 폼 초기화
function initializeForm() {
    document.getElementById('tastingForm').addEventListener('submit', function(e) {
        e.preventDefault();
        saveTastingNote();
    });
}

// 테이스팅 노트 저장
async function saveTastingNote() {
    if (!selectedWine) {
        alert('와인을 선택해주세요.');
        return;
    }

    if (currentRating === 0) {
        alert('별점을 선택해주세요.');
        return;
    }

    try {
        // 먼저 와인을 백엔드에 등록 (중복 체크)
        const wineData = {
            name: selectedWine.name,
            type: selectedWine.type.replace(' ', '_').toUpperCase(),
            region: selectedWine.region,
            producer: selectedWine.producer,
            vintageYear: null,
            alcoholContent: null,
            grapeVarieties: null
        };

        const token = localStorage.getItem('wine_diary_token');

        const wineResponse = await fetch('http://localhost:8080/api/v1/wines', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(wineData)
        });

        let wine;
        if (wineResponse.ok) {
            const wineResult = await wineResponse.json();
            wine = wineResult.data;
        } else {
            throw new Error('와인 등록에 실패했습니다.');
        }

        // 테이스팅 노트 저장
        const noteData = {
            wineId: wine.wineId,
            rating: currentRating,
            bodyLevel: parseInt(document.getElementById('body').value),
            tanninLevel: parseInt(document.getElementById('tannin').value),
            acidityLevel: parseInt(document.getElementById('acidity').value),
            sweetnessLevel: parseInt(document.getElementById('sweetness').value),
            notes: document.getElementById('notes').value,
            tastingDate: new Date().toISOString().split('T')[0]
        };

        const noteResponse = await fetch('http://localhost:8080/api/v1/tasting-notes', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(noteData)
        });

        if (noteResponse.ok) {
            alert('테이스팅 노트가 저장되었습니다!');
            window.location.href = 'dashboard.html';
        } else {
            throw new Error('테이스팅 노트 저장에 실패했습니다.');
        }

    } catch (error) {
        console.error('Error saving tasting note:', error);

        // 백엔드 연결 실패 시 로컬 스토리지에 저장
        const tastingNote = {
            id: Date.now(),
            user: currentUser,
            wine: selectedWine,
            characteristics: {
                body: document.getElementById('body').value,
                tannin: document.getElementById('tannin').value,
                acidity: document.getElementById('acidity').value,
                sweetness: document.getElementById('sweetness').value
            },
            rating: currentRating,
            notes: document.getElementById('notes').value,
            date: new Date().toISOString()
        };

        let savedNotes = JSON.parse(localStorage.getItem('wine_diary_notes') || '[]');
        savedNotes.push(tastingNote);
        localStorage.setItem('wine_diary_notes', JSON.stringify(savedNotes));

        alert('테이스팅 노트가 로컬에 저장되었습니다!');
        window.location.href = 'dashboard.html';
    }
}

// 로그아웃
function logout() {
    if (currentUser && currentUser.provider === 'kakao') {
        Kakao.Auth.logout();
    }

    localStorage.removeItem('wine_diary_user');
    window.location.href = 'index.html';
}

// 뒤로 가기
function goBack() {
    window.location.href = 'index.html';
}