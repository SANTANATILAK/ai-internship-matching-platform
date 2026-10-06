export const API_ENDPOINTS = {
  // Auth
  LOGIN: '/api/auth/login',
  REGISTER: '/api/auth/register',
  ME: '/api/auth/me',

  // User
  PROFILE: '/api/users/profile',

  // Resumes & ATS
  RESUME_UPLOAD: '/api/resumes/upload',
  RESUME_LATEST: '/api/resumes/latest',
  ATS_LATEST: '/api/ats/latest',
  ATS_HISTORY: '/api/ats/history',

  // Matches
  MATCHES_TOP: '/api/matching/top',
  MATCH_DETAIL: (id) => `/api/matching/opportunity/${id}`,

  // Opportunities
  OPPORTUNITIES: '/api/opportunities',
  OPPORTUNITY_DETAIL: (id) => `/api/opportunities/${id}`,
  OPPORTUNITIES_SEARCH: '/api/opportunities/search',
  OPPORTUNITIES_FILTER: '/api/opportunities/filter',
  OPPORTUNITIES_RECENT: '/api/opportunities/recent',
  OPPORTUNITIES_EXPIRING: '/api/opportunities/expiring',

  // Companies
  COMPANIES: '/api/companies',
  COMPANY_DETAIL: (id) => `/api/companies/${id}`,

  // Applications
  APPLICATIONS: '/api/applications',
  USER_APPLICATIONS: '/api/applications/user',
  APPLICATION_STATUS: (id) => `/api/applications/${id}/status`,

  // Saved / Bookmarks
  SAVED_OPPORTUNITIES: '/api/saved',
  SAVE_OPPORTUNITY: (id) => `/api/saved/${id}`,
  CHECK_SAVED: (id) => `/api/saved/check/${id}`,

  // Notifications
  NOTIFICATIONS: '/api/notifications',
  NOTIFICATION_READ: (id) => `/api/notifications/${id}/read`,
  NOTIFICATIONS_READ_ALL: '/api/notifications/read-all',

  // Admin
  ADMIN_STATS: '/api/admin/stats',
  ADMIN_USERS: '/api/admin/users',
  ADMIN_LOGS: '/api/admin/logs',
  ADMIN_COLLECTOR_RUN: '/api/admin/collector/run',
  ADMIN_COMPANIES: '/api/admin/companies',
  ADMIN_COMPANY_VERIFY: (id) => `/api/admin/companies/${id}/verify`,
  ADMIN_COMPANY_REJECT: (id) => `/api/admin/companies/${id}/reject`,
  ADMIN_OPPORTUNITIES: '/api/admin/opportunities',
  ADMIN_OPPORTUNITY_VERIFY: (id) => `/api/admin/opportunities/${id}/verify`,
  ADMIN_OPPORTUNITY_REJECT: (id) => `/api/admin/opportunities/${id}/reject`,
};
