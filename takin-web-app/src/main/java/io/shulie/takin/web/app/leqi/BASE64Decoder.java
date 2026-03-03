package io.shulie.takin.web.app.leqi;

public class BASE64Decoder {
    private static final int EIGHT_BIT_MASK = 255;

    private int mapCharToInt(char c) {
        /* 94*/         if (c >= 'A' && c <= 'Z') {
            /* 95*/             return c - 65;
        }
        /* 98*/         if (c >= 'a' && c <= 'z') {
            /* 99*/             return c - 97 + 26;
        }
        /*102*/         if (c >= '0' && c <= '9') {
            /*103*/             return c - 48 + 52;
        }
        /*106*/         if (c == '+') {
            /*107*/             return 62;
        }
        /*110*/         if (c == '/') {
            /*111*/             return 63;
        }
        throw new RuntimeException(c + " is not a valid Base64 character.");
    }

    public byte[] decodeBuffer(String data) {
        StringWrapper wrapper = new StringWrapper(data);
        /* 25*/         int byteArrayLength = wrapper.getUsefulLength() * 3 / 4;
        /* 27*/         byte[] result = new byte[byteArrayLength];
        /* 29*/         int byteTriplet = 0;
        /* 30*/         int byteIndex = 0;
        /* 34*/         while (byteIndex + 2 < byteArrayLength) {
            /* 38*/             byteTriplet = this.mapCharToInt(wrapper.getNextUsefulChar());
            /* 39*/             byteTriplet <<= 6;
            /* 40*/             byteTriplet |= this.mapCharToInt(wrapper.getNextUsefulChar());
            /* 41*/             byteTriplet <<= 6;
            /* 42*/             byteTriplet |= this.mapCharToInt(wrapper.getNextUsefulChar());
            /* 43*/             byteTriplet <<= 6;
            result[byteIndex + 2] = (byte)((byteTriplet |= this.mapCharToInt(wrapper.getNextUsefulChar())) & 0xFF);
            result[byteIndex + 1] = (byte)((byteTriplet >>= 8) & 0xFF);
            result[byteIndex] = (byte)((byteTriplet >>= 8) & 0xFF);
            /* 53*/             byteIndex += 3;
        }
        /* 57*/         if (byteIndex == byteArrayLength - 1) {
            /* 59*/             byteTriplet = this.mapCharToInt(wrapper.getNextUsefulChar());
            /* 60*/             byteTriplet <<= 6;
            /* 61*/             byteTriplet |= this.mapCharToInt(wrapper.getNextUsefulChar());
            result[byteIndex] = (byte)((byteTriplet >>= 4) & 0xFF);
        }
        /* 69*/         if (byteIndex == byteArrayLength - 2) {
            /* 71*/             byteTriplet = this.mapCharToInt(wrapper.getNextUsefulChar());
            /* 72*/             byteTriplet <<= 6;
            /* 73*/             byteTriplet |= this.mapCharToInt(wrapper.getNextUsefulChar());
            /* 74*/             byteTriplet <<= 6;
            /* 75*/             byteTriplet |= this.mapCharToInt(wrapper.getNextUsefulChar());
            result[byteIndex + 1] = (byte)((byteTriplet >>= 2) & 0xFF);
            result[byteIndex] = (byte)((byteTriplet >>= 8) & 0xFF);
        }
        /* 84*/         return result;
    }

    private class StringWrapper {
        private String mString;
        private int mIndex = 0;
        private int mUsefulLength;

        private boolean isUsefulChar(char c) {
            return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '+' || c == '/';
        }

        public int getUsefulLength() {
            return this.mUsefulLength;
        }

        public char getNextUsefulChar() {
            char result = '_';
            while (!this.isUsefulChar(result)) {
                result = this.mString.charAt(this.mIndex++);
            }
            return result;
        }

        public StringWrapper(String s) {
            this.mString = s;
            this.mUsefulLength = 0;
            int length = this.mString.length();
            for (int i = 0; i < length; ++i) {
                if (!this.isUsefulChar(this.mString.charAt(i))) continue;
                ++this.mUsefulLength;
            }
        }
    }
}
