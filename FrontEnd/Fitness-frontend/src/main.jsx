import { createRoot } from 'react-dom/client'
import { Provider } from 'react-redux'
import store from './store/store'
import './index.css'
import App from './app'
import { AuthProvider } from './auth/AuthProvider'
import { authConfig } from './authConfig'

createRoot(document.getElementById('root')).render(
  <AuthProvider authConfig={authConfig}
  loadingComponent={<div>Loading...</div>}
  >
    <Provider store={store}>
      <App />
    </Provider>
  </AuthProvider>,
)
