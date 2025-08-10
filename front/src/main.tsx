import * as Sentry from '@sentry/react';

Sentry.init({
  dsn: 'https://3ee328ba3b6db8dc2d9500cbf86fa395@o4509779953516544.ingest.us.sentry.io/4509779959021568',
  // Setting this option to true will send default PII data to Sentry.
  // For example, automatic IP address collection on events
  sendDefaultPii: true,
});

import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './index.css';
import { Toaster } from '@/components/shared/ui/sonner';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
    <Toaster />
  </React.StrictMode>
);
