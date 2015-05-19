package rizomm.vlille.utils;

import com.squareup.okhttp.MediaType;

import org.apache.commons.io.IOUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

import okio.Buffer;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by Maximilien on 19/02/2015.
 */
public class VlilleConverter implements Converter {
    private static final boolean DEFAULT_STRICT = true;
    private static final String CHARSET = "UTF-8";
    private static final MediaType MEDIA_TYPE =
            MediaType.parse("application/xml; charset=" + CHARSET);

    private final Serializer serializer;

    private final boolean strict;

    public VlilleConverter() {
        this(DEFAULT_STRICT);
    }

    public VlilleConverter(boolean strict) {
        this(new Persister(), strict);
    }

    public VlilleConverter(Serializer serializer) {
        this(serializer, DEFAULT_STRICT);
    }

    public VlilleConverter(Serializer serializer, boolean strict) {
        this.serializer = serializer;
        this.strict = strict;
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        InputStream is = null;
        try {
            is = body.in();
            String formatedEncoding = IOUtils.toString(is).replaceAll("utf-16", "utf-8");

            return serializer.read((Class<?>) type, IOUtils.toInputStream(formatedEncoding), strict);
        } catch (Exception e) {
            throw new ConversionException("Impossible to convert", e);
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    System.err.println("Impossible to close the stream");
                }
            }
        }

    }

    @Override
    public TypedOutput toBody(Object object) {
        byte[] bytes;
        try {
            Buffer buffer = new Buffer();
            OutputStreamWriter osw = new OutputStreamWriter(buffer.outputStream(), CHARSET);
            serializer.write(object, osw);
            osw.flush();
            bytes = buffer.readByteArray();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        return new TypedByteArray(MEDIA_TYPE.type(), bytes);
    }
}
