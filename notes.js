// 카카오 SDK 초기화
if (typeof Kakao !== 'undefined' && !Kakao.isInitialized()) {
    Kakao.init('YOUR_KAKAO_APP_KEY');
}

// 전역 변수
let currentUser = null;
let allNotes = [];
let filteredNotes = [];
let currentPage = 0;
let pageSize = 12;
let selectedNoteId = null;

// API 기본 URL
const API_BASE_URL = 'http://localhost:8080/api/v1';

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    //checkLoginStatus();
    initializeEventListeners();
    loadTastingNotes();
    loadStatistics();
});

// 로그인 상태 확인
function checkLoginStatus() {
    const savedUser = localStorage.getItem('wine_diary_user');
    const token = localStorage.getItem('wine_diary_token');

    if (!savedUser || !token) {
        alert('로그인이 필요합니다.');
        window.location.href = 'index.html';
        return;
    }

    currentUser = JSON.parse(savedUser);
    document.getElementById('user-name').textContent = `${currentUser.name}님`;
}

// 이벤트 리스너 초기화
function initializeEventListeners() {
    // 검색 기능
    const searchInput = document.getElementById('search-input');
    searchInput.addEventListener('input', debounce(handleSearch, 300));

    // 평점 필터
    const ratingFilters = document.querySelectorAll('.filter-btn');
    ratingFilters.forEach(btn => {
        btn.addEventListener('click', function() {
            ratingFilters.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            applyFilters();
        });
    });

    // 모달 외부 클릭 시 닫기
    document.getElementById('note-modal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeModal();
        }
    });
}

// 디바운스 함수
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 테이스팅 노트 로드
async function loadTastingNotes() {
    try {
        showLoading(true);
        const token = localStorage.getItem('wine_diary_token');

        const response = await fetch(`${API_BASE_URL}/tasting-notes/my`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const result = await response.json();
            allNotes = result.data || [];
            filteredNotes = [...allNotes];
            displayNotes();
        } else if (response.status === 401) {
            alert('로그인이 만료되었습니다.');
            localStorage.removeItem('wine_diary_token');
            window.location.href = 'index.html';
        } else {
            throw new Error('Failed to load notes');
        }
    } catch (error) {
        console.error('Error loading notes:', error);
        showError('노트를 불러오는데 실패했습니다.');
    } finally {
        showLoading(false);
    }
}

// 통계 로드
async function loadStatistics() {
    try {
        const token = localStorage.getItem('wine_diary_token');

        // 사용자 프로필 조회
        const profileResponse = await fetch(`${API_BASE_URL}/users/profile`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (profileResponse.ok) {
            const profile = await profileResponse.json();
            const userData = profile.data;

            document.getElementById('total-notes').textContent = userData.totalNotes || 0;
            document.getElementById('average-rating').textContent =
                userData.averageRating ? userData.averageRating.toFixed(1) : '0.0';
        }

        // 이번 달 노트 계산
        const thisMonth = getThisMonthCount();
        document.getElementById('this-month-notes').textContent = thisMonth;

    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

// 이번 달 노트 수 계산
function getThisMonthCount() {
    const now = new Date();
    const currentMonth = now.getMonth();
    const currentYear = now.getFullYear();

    return allNotes.filter(note => {
        const noteDate = new Date(note.createdAt);
        return noteDate.getMonth() === currentMonth &&
               noteDate.getFullYear() === currentYear;
    }).length;
}

// 노트 표시
function displayNotes() {
    const container = document.getElementById('notes-container');
    const emptyState = document.getElementById('empty-state');
    const pagination = document.getElementById('pagination');

    if (filteredNotes.length === 0) {
        container.style.display = 'none';
        pagination.style.display = 'none';
        emptyState.style.display = 'block';
        return;
    }

    container.style.display = 'grid';
    emptyState.style.display = 'none';

    // 페이지네이션 계산
    const totalPages = Math.ceil(filteredNotes.length / pageSize);
    const startIndex = currentPage * pageSize;
    const endIndex = Math.min(startIndex + pageSize, filteredNotes.length);
    const currentNotes = filteredNotes.slice(startIndex, endIndex);

    // 노트 카드 생성
    container.innerHTML = currentNotes.map(note => createNoteCard(note)).join('');

    // 페이지네이션 업데이트
    updatePagination(totalPages);
}

// 노트 카드 생성
function createNoteCard(note) {
    const ratingTexts = ['', '별로예요', '괜찮아요', '좋아요', '매우 좋아요', '최고예요'];
    const stars = '★'.repeat(note.rating) + '☆'.repeat(5 - note.rating);

    const notePreview = note.notes ?
        (note.notes.length > 100 ? note.notes.substring(0, 100) + '...' : note.notes) :
        '노트가 없습니다.';

    const tastingDate = new Date(note.tastingDate).toLocaleDateString('ko-KR');
    const createdAt = new Date(note.createdAt).toLocaleDateString('ko-KR');

    return `
        <div class="note-card" onclick="viewNoteDetail(${note.noteId})">
            <div class="note-header">
                <div class="wine-info">
                    <div class="wine-name">${note.wine.name}</div>
                    <div class="wine-details">${note.wine.type} · ${note.wine.region}</div>
                    <div class="wine-producer">${note.wine.producer}</div>
                </div>
                <div class="rating-display">
                    <div class="stars">${stars}</div>
                    <div class="rating-text">${ratingTexts[note.rating]}</div>
                </div>
            </div>

            <div class="note-preview">${notePreview}</div>

            <div class="note-footer">
                <div class="tasting-date">
                    <i class="fas fa-calendar"></i>
                    ${tastingDate}
                </div>
                <div class="note-actions">
                    <button class="action-btn" onclick="event.stopPropagation(); editNote(${note.noteId})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="action-btn" onclick="event.stopPropagation(); deleteNote(${note.noteId})">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        </div>
    `;
}

// 노트 상세 보기
async function viewNoteDetail(noteId) {
    try {
        const token = localStorage.getItem('wine_diary_token');

        const response = await fetch(`${API_BASE_URL}/tasting-notes/${noteId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const result = await response.json();
            const note = result.data;
            selectedNoteId = noteId;
            showNoteModal(note);
        } else {
            throw new Error('Failed to load note detail');
        }
    } catch (error) {
        console.error('Error loading note detail:', error);
        showError('노트 상세 정보를 불러오는데 실패했습니다.');
    }
}

// 모달 표시
function showNoteModal(note) {
    const modal = document.getElementById('note-modal');
    const ratingTexts = ['', '별로예요', '괜찮아요', '좋아요', '매우 좋아요', '최고예요'];

    // 기본 정보 설정
    document.getElementById('modal-wine-name').textContent = note.wine.name;
    document.getElementById('modal-wine-type').textContent = note.wine.type.replace('_', ' ');
    document.getElementById('modal-wine-region').textContent = note.wine.region;
    document.getElementById('modal-wine-producer').textContent = note.wine.producer;
    document.getElementById('modal-tasting-date').textContent =
        new Date(note.tastingDate).toLocaleDateString('ko-KR');

    // 별점 표시
    const stars = document.getElementById('modal-stars');
    stars.innerHTML = '';
    for (let i = 1; i <= 5; i++) {
        const star = document.createElement('span');
        star.className = i <= note.rating ? 'star active' : 'star';
        star.textContent = '★';
        stars.appendChild(star);
    }
    document.getElementById('modal-rating-text').textContent = ratingTexts[note.rating];

    // 특성 바 설정
    setCharacteristicBar('modal-body', note.bodyLevel);
    setCharacteristicBar('modal-tannin', note.tanninLevel);
    setCharacteristicBar('modal-acidity', note.acidityLevel);
    setCharacteristicBar('modal-sweetness', note.sweetnessLevel);

    // 노트 내용
    document.getElementById('modal-notes').textContent = note.notes || '노트가 없습니다.';

    // 모달 표시
    modal.style.display = 'flex';
    document.body.style.overflow = 'hidden';
}

// 특성 바 설정
function setCharacteristicBar(elementId, level) {
    const element = document.getElementById(elementId);
    if (element && level) {
        element.style.width = (level * 20) + '%';
    } else if (element) {
        element.style.width = '0%';
    }
}

// 모달 닫기
function closeModal() {
    const modal = document.getElementById('note-modal');
    modal.style.display = 'none';
    document.body.style.overflow = 'auto';
    selectedNoteId = null;
}

// 검색 처리
function handleSearch() {
    const searchTerm = document.getElementById('search-input').value.toLowerCase().trim();

    if (searchTerm === '') {
        filteredNotes = [...allNotes];
    } else {
        filteredNotes = allNotes.filter(note =>
            note.wine.name.toLowerCase().includes(searchTerm) ||
            note.wine.producer.toLowerCase().includes(searchTerm) ||
            note.wine.region.toLowerCase().includes(searchTerm)
        );
    }

    currentPage = 0;
    displayNotes();
}

// 필터 적용
function applyFilters() {
    const activeRatingFilter = document.querySelector('.filter-btn.active').dataset.rating;
    const periodFilter = document.getElementById('period-filter').value;
    const searchTerm = document.getElementById('search-input').value.toLowerCase().trim();

    filteredNotes = allNotes.filter(note => {
        // 검색 필터
        const matchesSearch = !searchTerm ||
            note.wine.name.toLowerCase().includes(searchTerm) ||
            note.wine.producer.toLowerCase().includes(searchTerm) ||
            note.wine.region.toLowerCase().includes(searchTerm);

        // 평점 필터
        const matchesRating = activeRatingFilter === 'all' ||
            note.rating >= parseInt(activeRatingFilter);

        // 기간 필터
        const matchesPeriod = matchesPeriodFilter(note, periodFilter);

        return matchesSearch && matchesRating && matchesPeriod;
    });

    currentPage = 0;
    displayNotes();
}

// 기간 필터 확인
function matchesPeriodFilter(note, period) {
    if (period === 'all') return true;

    const noteDate = new Date(note.createdAt);
    const now = new Date();
    const diffTime = now.getTime() - noteDate.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    switch (period) {
        case 'week': return diffDays <= 7;
        case 'month': return diffDays <= 30;
        case '3months': return diffDays <= 90;
        case 'year': return diffDays <= 365;
        default: return true;
    }
}

// 정렬
function sortNotes() {
    const sortBy = document.getElementById('sort-select').value;

    filteredNotes.sort((a, b) => {
        switch (sortBy) {
            case 'latest':
                return new Date(b.createdAt) - new Date(a.createdAt);
            case 'rating-high':
                return b.rating - a.rating;
            case 'rating-low':
                return a.rating - b.rating;
            case 'wine-name':
                return a.wine.name.localeCompare(b.wine.name);
            default:
                return 0;
        }
    });

    currentPage = 0;
    displayNotes();
}

// 기간별 필터링
function filterByPeriod() {
    applyFilters();
}

// 페이지네이션 업데이트
function updatePagination(totalPages) {
    const pagination = document.getElementById('pagination');
    const prevBtn = document.getElementById('prev-btn');
    const nextBtn = document.getElementById('next-btn');
    const pageInfo = document.getElementById('page-info');

    if (totalPages <= 1) {
        pagination.style.display = 'none';
        return;
    }

    pagination.style.display = 'flex';
    pageInfo.textContent = `${currentPage + 1} / ${totalPages}`;

    prevBtn.disabled = currentPage === 0;
    nextBtn.disabled = currentPage >= totalPages - 1;
}

// 이전 페이지
function loadPrevPage() {
    if (currentPage > 0) {
        currentPage--;
        displayNotes();
    }
}

// 다음 페이지
function loadNextPage() {
    const totalPages = Math.ceil(filteredNotes.length / pageSize);
    if (currentPage < totalPages - 1) {
        currentPage++;
        displayNotes();
    }
}

// 노트 수정
function editNote(noteId) {
    if (noteId) {
        selectedNoteId = noteId;
    }

    // 수정 페이지로 이동 (구현 필요)
    alert('수정 기능은 추후 구현 예정입니다.');
}

// 노트 삭제
async function deleteNote(noteId) {
    if (!noteId) {
        noteId = selectedNoteId;
    }

    if (!noteId) return;

    if (!confirm('이 테이스팅 노트를 삭제하시겠습니까?')) {
        return;
    }

    try {
        const token = localStorage.getItem('wine_diary_token');

        const response = await fetch(`${API_BASE_URL}/tasting-notes/${noteId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            alert('노트가 삭제되었습니다.');
            closeModal();
            loadTastingNotes();
            loadStatistics();
        } else {
            throw new Error('Failed to delete note');
        }
    } catch (error) {
        console.error('Error deleting note:', error);
        showError('노트 삭제에 실패했습니다.');
    }
}

// 로딩 표시/숨김
function showLoading(show) {
    const loading = document.getElementById('loading');
    loading.style.display = show ? 'block' : 'none';
}

// 에러 표시
function showError(message) {
    alert(message); // 추후 토스트 메시지로 개선 가능
}

// 로그아웃
function logout() {
    if (currentUser && currentUser.provider === 'kakao') {
        Kakao.Auth.logout();
    }

    localStorage.removeItem('wine_diary_user');
    localStorage.removeItem('wine_diary_token');
    alert('로그아웃되었습니다.');
    window.location.href = 'index.html';
}

// 뒤로 가기
function goBack() {
    window.location.href = 'index.html';
}