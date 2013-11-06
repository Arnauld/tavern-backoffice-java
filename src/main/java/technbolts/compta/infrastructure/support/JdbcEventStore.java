package technbolts.compta.infrastructure.support;

import com.google.common.collect.Lists;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technbolts.compta.infrastructure.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static technbolts.compta.infrastructure.support.JdbcUtils.closeQuietly;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JdbcEventStore implements EventStore {


    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DataSource dataSource;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JdbcEventStore(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void store(@Nonnull Id streamId, @Nonnull Stream<VersionedDomainEvent> stream) {
        Connection connection = null;
        PreparedStatement pStmt = null;
        try {
            connection = dataSource.getConnection();
            String sql = "INSERT INTO events (entity_id, version, creation_ts, event_type, payload) VALUES (?, ?, ?, ?, ?)";
            pStmt = connection.prepareStatement(sql);
            pStmt.setString(1, streamId.asString());

            InsertEvent insertEvent = new InsertEvent(pStmt, objectMapper);
            stream.consume(insertEvent);

            log.debug("Stream {}: {} event(s) inserted", streamId, insertEvent.nbInsert);
        } catch (SQLException e) {
            throw new DataStoreException("Ouch", e);
        } finally {
            closeQuietly(pStmt);
            closeQuietly(connection);
        }
    }

    private static class InsertEvent implements SideEffect<VersionedDomainEvent> {
        private final PreparedStatement pStmt;
        private final ObjectMapper objectMapper;
        private int nbInsert = 0;

        private InsertEvent(PreparedStatement pStmt, ObjectMapper objectMapper) {
            this.pStmt = pStmt;
            this.objectMapper = objectMapper;
        }

        @Override
        public void apply(VersionedDomainEvent event) {
            try {
                DomainEvent dEvent = event.domainEvent();

                String eventAsJson = objectMapper.writeValueAsString(dEvent);
                pStmt.setLong(2, event.version());
                pStmt.setTimestamp(3, new Timestamp(event.creationTimestamp()));
                pStmt.setString(4, dEvent.getClass().getName());
                pStmt.setString(5, eventAsJson);
                pStmt.executeUpdate();

                nbInsert++;
            } catch (Exception e) {
                throw new DataStoreException("Fail to insert event... " + event, e);
            }
        }
    }

    @Nullable
    @Override
    public Stream<VersionedDomainEvent> openStream(@Nonnull Id streamId) {
        Connection connection = null;
        PreparedStatement pStmt = null;
        try {
            connection = dataSource.getConnection();
            String sql = "SELECT version, creation_ts, event_type, payload FROM events WHERE entity_id = ?";
            pStmt = connection.prepareStatement(sql);
            pStmt.setString(1, streamId.asString());
            ResultSet resultSet = pStmt.executeQuery();

            List<VersionedDomainEvent> events = Lists.newArrayList();
            while (resultSet.next()) {
                long version = resultSet.getLong(1);
                long ts = resultSet.getTimestamp(2).getTime();

                String eventType = resultSet.getString(3);
                String eventAsJson = resultSet.getString(4);

                log.debug("Stream {} - Event loaded v{}, ts: {}, type: {}, json: {}", streamId, version, ts, eventType, eventAsJson);

                Class<?> eventClass = Class.forName(eventType);
                DomainEvent event = (DomainEvent) objectMapper.readValue(eventAsJson, eventClass);

                events.add(new VersionedDomainEvent(event, version, ts));
            }
            return Streams.from(events);
        } catch (SQLException e) {
            throw new DataStoreException("Ouch", e);
        } catch (ClassNotFoundException e) {
            throw new DataStoreException("Ouch", e);
        } catch (JsonMappingException e) {
            throw new DataStoreException("Ouch", e);
        } catch (JsonParseException e) {
            throw new DataStoreException("Ouch", e);
        } catch (IOException e) {
            throw new DataStoreException("Ouch", e);
        } finally {
            closeQuietly(pStmt);
            closeQuietly(connection);
        }
    }
}
