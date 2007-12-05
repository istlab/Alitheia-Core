package eu.sqooss.impl.service.messaging.senders.smtp.utils;

import java.io.*;

public class Base64 {
    private final static int BASE_CHARS_PER_LINE = 76;
    private final static byte MASK = 0x3f;
    private final static byte PADDING = (byte) '=';

//  private final static char codes1[] = {
//  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
//  'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
//  'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
//  'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
//  'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
//  'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
//  'w', 'x', 'y', 'z', '0', '1', '2', '3',
//  '4', '5', '6', '7', '8', '9', '+', '/'
//  };

    /**
     * Auxiliary array containing int values of chars.
     */
    public static byte codes[] = new byte[] {
        65,  66,  67,  68,  69,  70,  71,  72,
        73,  74,  75,  76,  77,  78,  79,  80, 
        81,  82,  83,  84,  85,  86,  87,  88, 
        89,  90,  97,  98 , 99, 100, 101, 102,
        103, 104, 105, 106, 107, 108, 109, 110,
        111, 112, 113, 114, 115, 116, 117, 118, 
        119, 120, 121, 122,  48,  49,  50,  51, 
        52,  53,  54,  55,  56,  57,  43,  47
    };

    /**
     * Encodes byte array. Result is with 76 simbols per line.
     * 
     * @param b_in byte array data for encoding.
     * @return encoded data.
     */
    public static byte[] encode(byte[] b_in) {
        return encode(b_in, BASE_CHARS_PER_LINE);
    }

    public static byte[] encode(byte[] b_in, int off, int length) {
        return encode(b_in, off, length, BASE_CHARS_PER_LINE);
    }

    public static byte[] encode(byte[] b_in, int CHARS_PER_LINE) {
        return encode(b_in, 0, b_in.length, CHARS_PER_LINE );
    }

    /**
     * Encodes byte array. Result is with CHARS_PER_LINE simbols per line.
     * 
     * @param b_in byte array data for encoding.
     * @param CHARS_PER_LINE chars per line in output.
     * @return encoded data.
     */  
    public static byte[] encode(byte[] b_in, int off1, int length, int CHARS_PER_LINE) {
        int atomLen = (length - 1) / 3 + 1;
        byte[] b_res = new byte[atomLen * 4 + 2];
        for (int i = 0; i < off1 + length / 3; i++) {
            encodeAtom(b_in, off1+i*3, b_res, i * 3);
        }
        // if there are left some bytes
        int left = length % 3;
        int off = length - left;
        if (left != 0) {
            b_res[b_res.length - 6] = codes[(b_in[off] >>> 2) & MASK];
            if (left == 1) {
                b_res[b_res.length - 5] = codes[(b_in[off] << 4) & 0x30];
                b_res[b_res.length - 4] = PADDING;
            } else {
                b_res[b_res.length - 5] = codes[((b_in[off] << 4) & 0x30) + ((b_in[off+1] >>> 4) & 0x0f)];
                b_res[b_res.length - 4] = codes[((b_in[off+1]) & 0x0f) << 2];
            }
            b_res[b_res.length - 3] = PADDING;      
        }
        //put the ending CRLF
        b_res[b_res.length - 2] = 13;
        b_res[b_res.length - 1] = 10;    

        if (b_res.length <= CHARS_PER_LINE + 2) {
            return b_res;
        }

        int blocks = (b_res.length-2) / CHARS_PER_LINE;
        left = (b_res.length-2) % CHARS_PER_LINE;    
        byte[] b_res1 = new byte[b_res.length + ((left == 0) ? 2*(blocks-1) : 2 *blocks )];    
        off = 0;
        for (int i = 0; i < blocks; i++) {
            System.arraycopy(b_res, i * CHARS_PER_LINE, b_res1, off, CHARS_PER_LINE);
            off += CHARS_PER_LINE;
            if (!((i == (blocks-1)) && (left == 0))) {
                b_res1[off++] = (byte) 13;
                b_res1[off++] = (byte) 10;
            }
        }
        System.arraycopy(b_res, blocks * CHARS_PER_LINE, b_res1, off, left +2);
        return b_res1;
    } // encode()


    public static byte[] decode(byte[] data) throws Exception  {
        return decode(data, 0, data.length);
    }

    /**
     * Decodes Base64 encoded data.
     * 
     * @param b_in encoded data.
     * @return decoded data.
     * @exception when data is not in correct Base64 format.
     */	
    public static byte[] decode(byte[] b_in, int off, int length) throws Exception {    
        b_in = readEncoded(b_in, off, length);

        if ((b_in.length & 0x03) != 0)
            throw new Exception("decode exception: input array size must be multyple by 4.");

        int len = b_in.length;
        int atomLen = len >>> 2;
        byte[] b_res;
        int padding = 0;

        if (b_in[len - 1] == (byte) PADDING)
            padding++; // 1 or 2 chars
        if (b_in[len - 2] == (byte) PADDING)
            padding++; // 1 char, otherwise - 2
        if (b_in[len - 3] == (byte) PADDING)
            throw new Exception("decode exception: invalid input data.");

        b_res = new byte[atomLen * 3 - padding];
        for (int i = 0, j = 0; i < b_in.length - 4; ) {
            decodeAtom(b_res, b_in, i, j);
            i += 4; 
            j += 3;
        }

        // if there are more symbols
        if (padding != 0) {
            int tmp = 0;
            tmp |= getIndex(b_in[len - 4]);
            if (padding == 2) {
                tmp <<= 2;
                tmp |= (getIndex(b_in[len - 3]) >>> 4);
            } else /* 1 more byte do decode*/ {
                tmp <<= 6;
                tmp |= getIndex(b_in[len - 3]);
                tmp <<= 4;
                tmp |= (getIndex(b_in[len - 2]) >>> 2);
                b_res[b_res.length - 2] = (byte) ((tmp >> 8) & 0xff);
            }
            b_res[b_res.length - 1] = (byte) (tmp & 0xff);
        } else {
            decodeAtom(b_res, b_in, b_in.length - 4, b_res.length - 3);
        }

        return b_res;
    } // decode()



////////////////////END OF PUBLIC METHODS///////////////////////////////////	


    private static void encodeAtom(byte[] b_in, int pos1, byte[] b_out, int pos2) {
        int temp = b_in[pos1] & 0xff;
        temp <<= 8;
        temp |= b_in[pos1 + 1] & 0xff;
        temp <<= 8;
        temp |= b_in[pos1 + 2] & 0xff;

        int local = (pos2 / 3) << 2; 
        b_out[local++] = codes[(temp >> 18) & MASK];
        b_out[local++] = codes[(temp >> 12) & MASK];
        b_out[local++] = codes[(temp >> 6) & MASK];
        b_out[local] = codes[temp & MASK];
    } // endcodeAtom()


//  private static void encodeAtom(byte[] b_out, byte[] b_in, int off) {
//  int temp = b_in[off] & 0xff;
//  temp <<= 8;
//  temp |= b_in[off + 1] & 0xff;
//  temp <<= 8;
//  temp |= b_in[off + 2] & 0xff;
//  int local = (off / 3) << 2; 
//  b_out[local++] = codes[(temp >> 18) & MASK];
//  b_out[local++] = codes[(temp >> 12) & MASK];
//  b_out[local++] = codes[(temp >> 6) & MASK];
//  b_out[local] = codes[temp & MASK];
//  } // endcodeAtom()

    private static void decodeAtom(byte[] b_out, byte[] b_in, int off1, int off2) throws Exception {
        if ((off1 < 0) || (b_in.length <= off1))
            throw new Exception("decodeAtom exception: invalid offset " + off1);
        int tmp = 0;
        for (int i = 0; i < 4; i++) {
            tmp <<= 6;
            tmp |= (0xff & getIndex(b_in[i + off1]));
        }
        b_out[off2++] = (byte) ((tmp >> 16) & 0xff);
        b_out[off2++] = (byte) ((tmp >> 8) & 0xff);
        b_out[off2] = (byte) (tmp & 0xff);
    } // decodeAtom()

    private static int getIndex(byte b)
    throws Exception
    {
        // 'A' - 0
        // 'a' - 26
        // '0' - 52
        // '+' - 62
        // '/' - 63
        if ((b > 64) && (b < 91)) {
            return b - 65;
        }
        if ((b > 96) && (b < 123)) {
            return b - 71;
        }
        if ((b > 47) && (b < 58)) {
            return b + 4;
        }
        if (b == 43) {
            return 62;
        }
        if (b == 47) {
            return 63;
        }
        throw new Exception("decodeAtom exception: unknown symbol " + b);
    } // getIndex()



    private static byte[] readEncoded(byte[] b, int start, int length) throws Exception {
        byte[] b_tmp1 = new byte[length];
        int off = 0;
        for (int i = start; i < start+length; i++) {
            if ((b[i] >= '0' && b[i] <= '9') || 
                    (b[i] >= 'A' && b[i] <= 'Z') || 
                    (b[i] >= 'a' && b[i] <= 'z') || 
                    (b[i] == '/') || 
                    (b[i] == '+') || 
                    (b[i] == '=')) {

                b_tmp1[off++] = b[i];
            }
        }
        byte[] b_res = new byte[off];
        System.arraycopy(b_tmp1, 0, b_res, 0, off);
        return b_res;
    }	
} // Base64
