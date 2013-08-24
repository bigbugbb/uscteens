package edu.neu.android.mhealth.uscteensver1.data;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class AccelDataOutputStream extends ObjectOutputStream {

    private static File sFile;

    /**
     * initialize file object, and return the stream itself
     *
     * @param file file object, used for initialize the static file object
     * @param out  the output stream
     * @return AccelDataOutputStream
     * @throws IOException
     */
    public static AccelDataOutputStream getInstance(File file, OutputStream out)
            throws IOException {
        sFile = file;
        return new AccelDataOutputStream(out, sFile);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        if (!sFile.exists() || (sFile.exists() && sFile.length() == 0)) {
            super.writeStreamHeader();
        } else {
            super.reset();
        }

    }

    public AccelDataOutputStream(OutputStream out, File f) throws IOException {
        super(out);
    }
}
