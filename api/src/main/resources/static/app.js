'use strict';

const state = {
    conversationId: null,
    dialogId: null,
    currentQuestion: null,
};

const views = {
    login: document.getElementById('login-view'),
    issues: document.getElementById('issues-view'),
    question: document.getElementById('question-view'),
    messages: document.getElementById('messages-view'),
    error: document.getElementById('error-view'),
};

const els = {
    sessionInfo: document.getElementById('session-info'),
    conversationIdLabel: document.getElementById('conversation-id'),
    loginForm: document.getElementById('login-form'),
    resumeId: document.getElementById('resume-id'),
    btnRefresh: document.getElementById('btn-refresh'),
    btnAllIssues: document.getElementById('btn-all-issues'),
    btnLogout: document.getElementById('btn-logout'),
    issuesHeading: document.getElementById('issues-heading'),
    issuesList: document.getElementById('issues-list'),
    issuesEmpty: document.getElementById('issues-empty'),
    questionText: document.getElementById('question-text'),
    questionDialogId: document.getElementById('question-dialog-id'),
    answerForm: document.getElementById('answer-form'),
    answerInputContainer: document.getElementById('answer-input-container'),
    messagesList: document.getElementById('messages-list'),
    errorText: document.getElementById('error-text'),
};

function show(view) { view.hidden = false; }
function hide(view) { view.hidden = true; }

function resetTransientViews() {
    hide(views.issues);
    hide(views.question);
    hide(views.messages);
    hide(views.error);
}

async function postJson(path, body) {
    const init = { method: 'POST' };
    if (body !== undefined) {
        init.headers = { 'Content-Type': 'application/json' };
        init.body = JSON.stringify(body);
    }
    const response = await fetch(path, init);
    if (!response.ok) {
        throw new Error(`${response.status} ${response.statusText}`);
    }
    return response.json();
}

function isQuestionResponse(payload) {
    return payload && typeof payload.text === 'string'
        && Object.prototype.hasOwnProperty.call(payload, 'answerType');
}

function isIssuesResponse(payload) {
    return payload && Array.isArray(payload.issues);
}

function handleResponse(payload, opts = {}) {
    resetTransientViews();
    if (isQuestionResponse(payload)) {
        renderQuestion(payload);
    } else if (isIssuesResponse(payload)) {
        renderIssues(payload, opts.heading || 'Open Issues');
    } else {
        renderError('Unrecognized server response.');
    }
    renderMessages(payload && payload.messages);
}

function statusClass(status) {
    if (!status) return 'not-started';
    const s = String(status).toLowerCase();
    if (s.includes('successfully')) return 'resolved-successfully';
    if (s.includes('problems'))     return 'resolved-with-problems';
    if (s.includes('progress'))     return 'in-progress';
    return 'not-started';
}

function renderIssues(payload, heading) {
    state.conversationId = payload.conversationId || state.conversationId;
    state.dialogId = null;
    updateSessionInfo();

    els.issuesHeading.textContent = heading;
    els.issuesList.innerHTML = '';

    const items = payload.issues || [];
    if (items.length === 0) {
        show(views.issues);
        els.issuesEmpty.hidden = false;
        return;
    }

    els.issuesEmpty.hidden = true;
    for (const issue of items) {
        const li = document.createElement('li');
        const label = document.createElement('span');
        const status = document.createElement('span');

        if (typeof issue === 'string') {
            label.textContent = issue;
            status.textContent = 'Not Started';
            status.className = 'status not-started';
        } else {
            label.textContent = issue.label || issue.id || JSON.stringify(issue);
            const text = issue.status || 'Not Started';
            status.textContent = text;
            status.className = 'status ' + statusClass(text);
        }

        li.appendChild(label);
        li.appendChild(status);
        els.issuesList.appendChild(li);
    }
    show(views.issues);
}

function renderQuestion(payload) {
    state.dialogId = payload.dialogId || state.dialogId || 'dialog1';
    state.currentQuestion = payload;
    updateSessionInfo();

    els.questionText.textContent = payload.text || '';
    els.questionDialogId.textContent = state.dialogId ? `Dialog: ${state.dialogId}` : '';
    els.answerInputContainer.innerHTML = '';

    const type = payload.answerType;
    if (type === 'CHOICE') {
        const wrap = document.createElement('div');
        wrap.className = 'choices';
        (payload.choices || []).forEach((choice, idx) => {
            const id = `choice-${idx}`;
            const label = document.createElement('label');
            const radio = document.createElement('input');
            radio.type = 'radio';
            radio.name = 'answer';
            radio.value = choice;
            radio.id = id;
            if (idx === 0) radio.checked = true;
            const span = document.createElement('span');
            span.textContent = choice;
            label.appendChild(radio);
            label.appendChild(span);
            wrap.appendChild(label);
        });
        els.answerInputContainer.appendChild(wrap);
    } else if (type === 'INTEGER' || type === 'DOUBLE') {
        const input = document.createElement('input');
        input.type = 'number';
        input.name = 'answer';
        if (type === 'INTEGER') input.step = '1';
        input.required = true;
        els.answerInputContainer.appendChild(input);
    } else {
        const input = document.createElement('input');
        input.type = 'text';
        input.name = 'answer';
        input.required = true;
        els.answerInputContainer.appendChild(input);
    }

    show(views.question);
}

function renderMessages(messages) {
    els.messagesList.innerHTML = '';
    if (!messages || messages.length === 0) {
        hide(views.messages);
        return;
    }
    for (const m of messages) {
        const li = document.createElement('li');
        li.textContent = m;
        els.messagesList.appendChild(li);
    }
    show(views.messages);
}

function renderError(message) {
    els.errorText.textContent = message;
    show(views.error);
}

function updateSessionInfo() {
    if (state.conversationId) {
        els.conversationIdLabel.textContent = state.conversationId;
        show(els.sessionInfo);
    } else {
        hide(els.sessionInfo);
    }
}

function readAnswerValue(form) {
    const data = new FormData(form);
    const value = data.get('answer');
    return value == null ? '' : String(value);
}

function answerLabel() {
    return (state.currentQuestion && state.currentQuestion.label)
        || 'answer';
}

async function login(resumeId) {
    const path = resumeId
        ? `/login/${encodeURIComponent(resumeId)}`
        : '/login/';
    try {
        const payload = await postJson(path);
        state.conversationId = payload.conversationId || resumeId || null;
        hide(views.login);
        handleResponse(payload);
    } catch (e) {
        renderError(`Login failed: ${e.message}`);
    }
}

async function submitAnswer(value) {
    if (!state.conversationId || !state.dialogId) {
        renderError('No active dialog to answer.');
        return;
    }
    const body = { label: answerLabel(), value };
    try {
        const payload = await postJson(
            `/answers/${encodeURIComponent(state.conversationId)}/${encodeURIComponent(state.dialogId)}`,
            body
        );
        handleResponse(payload);
    } catch (e) {
        renderError(`Answer failed: ${e.message}`);
    }
}

async function refresh() {
    if (!state.conversationId) return;
    try {
        const payload = await postJson(`/refresh/${encodeURIComponent(state.conversationId)}`);
        state.dialogId = null;
        state.currentQuestion = null;
        handleResponse(payload, { heading: 'Open Issues' });
    } catch (e) {
        renderError(`Refresh failed: ${e.message}`);
    }
}

async function allIssues() {
    if (!state.conversationId) return;
    try {
        const payload = await postJson(`/all-issues/${encodeURIComponent(state.conversationId)}`);
        handleResponse(payload, { heading: 'All Issues' });
    } catch (e) {
        renderError(`All-issues failed: ${e.message}`);
    }
}

function logout() {
    state.conversationId = null;
    state.dialogId = null;
    state.currentQuestion = null;
    els.resumeId.value = '';
    updateSessionInfo();
    resetTransientViews();
    show(views.login);
}

els.loginForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const id = els.resumeId.value.trim();
    login(id || null);
});

els.answerForm.addEventListener('submit', (e) => {
    e.preventDefault();
    submitAnswer(readAnswerValue(els.answerForm));
});

els.btnRefresh.addEventListener('click', refresh);
els.btnAllIssues.addEventListener('click', allIssues);
els.btnLogout.addEventListener('click', logout);
