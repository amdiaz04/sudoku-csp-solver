# Deploying to Google Cloud Run

The API and frontend are deployed as two separate Cloud Run services, each built from its own `Dockerfile`. Requires the [gcloud CLI](https://cloud.google.com/sdk/docs/install) and a Google Cloud project with billing enabled.

**One-time setup:**
```bash
gcloud auth login
gcloud config set project YOUR_PROJECT_ID
gcloud services enable run.googleapis.com cloudbuild.googleapis.com artifactregistry.googleapis.com
```

**1. Deploy the API** (from the repo root). Note the service URL it prints:
```bash
gcloud run deploy sudoku-api --source product --region europe-west2 --allow-unauthenticated --max-instances 1
```

**2. Deploy the frontend**, pointing it at the API URL from step 1:
```bash
gcloud run deploy sudoku-frontend --source product/sudoku-frontend --region europe-west2 --allow-unauthenticated --set-env-vars API_URL=https://YOUR-API-URL
```

The frontend's nginx serves the React app and forwards `/api/` requests to `API_URL`, so the browser only talks to the frontend. `--max-instances 1` is used because the API keeps the current puzzle in memory, so a single instance keeps requests consistent.
