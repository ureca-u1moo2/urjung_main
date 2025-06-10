from fastapi import FastAPI, Request
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer

# 입력된 문장을 벡터로 변환해주는 API 서버
app = FastAPI()
# KoBERT 모델 사용
model = SentenceTransformer("snunlp/KR-SBERT-V40K-klueNLI-augSTS")

class TextRequest(BaseModel):
    text: str

@app.post("/embedding")
def get_embedding(req: TextRequest):
    embedding = model.encode(req.text).tolist()
    return embedding

