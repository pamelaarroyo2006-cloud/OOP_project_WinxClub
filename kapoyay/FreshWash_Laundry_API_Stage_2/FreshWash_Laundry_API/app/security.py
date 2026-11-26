import base64
import hashlib
import hmac
import json
import secrets
import time
from app.config import SECRET_KEY, ACCESS_TOKEN_EXPIRE_MINUTES

def _b64encode(data: bytes) -> str:
    return base64.urlsafe_b64encode(data).rstrip(b"=").decode("ascii")

def _b64decode(value: str) -> bytes:
    return base64.urlsafe_b64decode(value + "=" * (-len(value) % 4))

def hash_password(password: str) -> str:
    salt = secrets.token_bytes(16)
    digest = hashlib.pbkdf2_hmac("sha256", password.encode("utf-8"), salt, 260_000)
    return f"pbkdf2_sha256${salt.hex()}${digest.hex()}"

def verify_password(password: str, stored_hash: str) -> bool:
    try:
        scheme, salt_hex, digest_hex = stored_hash.split("$")
        if scheme != "pbkdf2_sha256":
            return False
        salt = bytes.fromhex(salt_hex)
        expected = bytes.fromhex(digest_hex)
        actual = hashlib.pbkdf2_hmac("sha256", password.encode("utf-8"), salt, 260_000)
        return hmac.compare_digest(actual, expected)
    except (ValueError, TypeError):
        return False

def create_access_token(user_id: int, role: str) -> str:
    header = {"alg": "HS256", "typ": "JWT"}
    payload = {"sub": str(user_id), "role": role, "exp": int(time.time()) + ACCESS_TOKEN_EXPIRE_MINUTES * 60}
    signing_input = f"{_b64encode(json.dumps(header, separators=(',', ':')).encode())}.{_b64encode(json.dumps(payload, separators=(',', ':')).encode())}"
    signature = hmac.new(SECRET_KEY.encode(), signing_input.encode(), hashlib.sha256).digest()
    return f"{signing_input}.{_b64encode(signature)}"

def decode_access_token(token: str) -> dict:
    parts = token.split(".")
    if len(parts) != 3:
        raise ValueError("Invalid token")
    signing_input = f"{parts[0]}.{parts[1]}"
    expected = hmac.new(SECRET_KEY.encode(), signing_input.encode(), hashlib.sha256).digest()
    supplied = _b64decode(parts[2])
    if not hmac.compare_digest(expected, supplied):
        raise ValueError("Invalid token signature")
    header = json.loads(_b64decode(parts[0]))
    payload = json.loads(_b64decode(parts[1]))
    if header.get("alg") != "HS256" or int(payload.get("exp", 0)) <= int(time.time()):
        raise ValueError("Expired or unsupported token")
    return payload
