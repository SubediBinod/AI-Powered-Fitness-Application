export const authConfig= {
  clientId: 'oauth-pkce-client',
  authorizationEndpoint: 'https://myAuthProvider.com/auth',
  tokenEndpoint: 'https://myAuthProvider.com/token',
  redirectUri: 'http://localhost:5173/',
  scope: 'openid profile email offline_access',
  onRefreshTokenExpire: (event) => event.logIn(),
}