@echo off
py -m venv .venv
call .venv\Scripts\activate
python -m pip install -r requirements.txt
uvicorn app.main:app --reload
