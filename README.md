```md
# NFC Tool (Android)

A lightweight **NFC study + utility app** for reading, analyzing, writing, and emulating NFC tags.

---

# 🚀 Overview

This project acts as a **mini NFC lab on your phone**:

```

Reader + Analyzer + Writer + Emulator

```

You built a tool that combines:

- Tag inspector (like TagInfo)
- Hex viewer
- MIFARE Classic analyzer
- APDU console
- HCE (Host Card Emulation)

---

# 🔧 Features

## 📖 Reading
- UID (Unique ID)
- Tag technologies (NfcA, MifareClassic, IsoDep, etc.)
- NDEF records
- Raw HEX + ASCII

## ✍️ Writing
- Write Text
- Write URL
- Write Phone number
- Safe NDEF writing

## 🧱 MIFARE Classic
- Sector / block viewer
- HEX + ASCII data
- Sector trailer decoding
- Access Bits display

## 🔍 Tools
- HEX viewer (low-level)
- APDU console (pro-level)
- Format NDEF tags

## 📲 HCE (Card Emulation)
- Phone acts as NFC card
- Send custom data
- Two-phone communication

---

# 🧠 NFC Concepts (Core Learning)

## NFC Stack

```

Hardware → Tag → Technology → Protocol → Data

```

| Layer | Example |
|------|--------|
| Technology | NfcA, MifareClassic, IsoDep |
| Protocol | APDU |
| Data | NDEF, RAW |

---

# 🏷 NFC Tag Types

```

MIFARE Classic → Memory blocks (low-level)
NTAG / NDEF    → Structured data
IsoDep         → Smart card (APDU)
HCE            → Phone as card

```

---

# 📦 MIFARE Classic Deep Dive

## Memory Structure

```

1K Card:
16 sectors
4 blocks per sector
16 bytes per block

```

## Layout

```

Block 0-2 → Data
Block 3   → Sector Trailer

```

## Sector Trailer

```

[ Key A (6 bytes) ]
[ Access Bits (4 bytes) ]
[ Key B (6 bytes) ]

```

---

# 🔐 Access Bits (VERY IMPORTANT)

Access Bits control:

- Read permission
- Write permission
- Key A / Key B usage
- Block locking

### Example

```

78 77 88 69

```

Typical meaning:

```

✔ Data blocks → Read/Write allowed
✔ Trailer → Protected

```

⚠️ Critical rule:

```

Wrong access bits = permanent lock (no recovery)

```

---

# 🔄 APDU (Smart Card Commands)

## Structure

```

CLA | INS | P1 | P2 | Lc | Data | Le

```

## Example Commands

```

00 A4 04 00 → SELECT
00 CA 00 00 → GET DATA

```

---

# 📲 HCE (Host Card Emulation)

Your phone behaves like a smart card.

## Communication Flow

```

Reader → SELECT AID
Card   → HCE_READY

Reader → GET DATA
Card   → Returns saved data

```

## Example

```

SELECT:
00 A4 04 00 07 F0 01 02 03 04 05 06 00

GET DATA:
00 CA 00 00 00

````

---

# 📊 Features Summary

| Feature | Description |
|--------|------------|
| READ | Full tag scan |
| WRITE | Write NDEF |
| HEX | Raw memory |
| MIFARE | Sector/block view |
| APDU | Send commands |
| FORMAT | Reset tag |
| HCE READ | Read another phone |
| ACT CARD | Emulate NFC card |

---

# 🧪 Testing Setup

## Recommended Devices

- Android phone with NFC
- Another phone (for HCE testing)

## Recommended Tags

- NTAG213 / 215 / 216
- MIFARE Classic (test cards)

---

# 📌 Permissions

```xml
<uses-permission android:name="android.permission.NFC" />

<uses-feature android:name="android.hardware.nfc" />
<uses-feature android:name="android.hardware.nfc.hce" />
````

---

# ⚠️ Limitations

```
✖ Cannot emulate MIFARE Classic
✖ Cannot crack keys
✖ Cannot bypass access bits
✖ Cannot modify secure cards
```

---

# 🎯 What You Achieved

You built:

```
✔ NFC Reader
✔ Memory Analyzer
✔ Data Writer
✔ APDU Terminal
✔ NFC Card Emulator
```

This is basically a **starter-level professional NFC toolkit**.

---

# 🧠 What You Now Understand

```
✔ NFC memory structure
✔ MIFARE sectors & blocks
✔ Access Bits (permissions)
✔ NDEF format
✔ APDU communication
✔ HCE card emulation
✔ Reader ↔ Card data exchange
```

---

# 🚀 Next Improvements

```
→ Full Access Bit decoder (visual)
→ Advanced NDEF parser
→ Safe block writer
→ APDU scripting UI
→ Tag type auto detection
→ Export scan reports
→ UI improvements (tabs like TagInfo)
```

---

# ⚠️ Ethics

Use only:

```
✔ Your own tags
✔ Lab/testing cards
```

Do NOT use for:

```
✖ Payment cards
✖ Access systems
✖ Unauthorized cloning
```

---

# 🏁 Final Summary

```
This app = NFC Learning Lab

Reader + Analyzer + Writer + Emulator
```

You are now at a level where you **understand real NFC internals**, not just usage.

---

🔥 If you continue:

You move from:

```
User → Developer → NFC Specialist
```
