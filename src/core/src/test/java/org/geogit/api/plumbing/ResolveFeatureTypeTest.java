package org.geogit.api.plumbing;

import org.geogit.api.RevFeatureType;
import org.geogit.api.RevObject.TYPE;
import org.geogit.api.porcelain.CommitOp;
import org.geogit.test.integration.RepositoryTestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Optional;

public class ResolveFeatureTypeTest extends RepositoryTestCase {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Override
    protected void setUpInternal() throws Exception {
        repo.getConfigDatabase().put("user.name", "groldan");
        repo.getConfigDatabase().put("user.email", "groldan@opengeo.org");
    }

    @Test
    public void testResolveFeatureType() throws Exception {
        insertAndAdd(points1);
        geogit.command(CommitOp.class).setMessage("Commit1").call();

        Optional<RevFeatureType> featureType = geogit.command(ResolveFeatureType.class)
                .setFeatureType(pointsName).call();
        assertTrue(featureType.isPresent());
        assertEquals(pointsTypeName, featureType.get().getName());
        assertEquals(TYPE.FEATURETYPE, featureType.get().getType());
    }

    @Test
    public void testResolveFeatureTypeWithColonInFeatureTypeName() throws Exception {
        insertAndAdd(points1);
        geogit.command(CommitOp.class).setMessage("Commit1").call();

        Optional<RevFeatureType> featureType = geogit.command(ResolveFeatureType.class)
                .setFeatureType("WORK_HEAD:" + pointsName).call();
        assertTrue(featureType.isPresent());
        assertEquals(pointsTypeName, featureType.get().getName());
        assertEquals(TYPE.FEATURETYPE, featureType.get().getType());
    }

    @Test
    public void testNoFeatureTypeNameSpecified() {
        exception.expect(IllegalStateException.class);
        geogit.command(ResolveFeatureType.class).call();
    }

    @Test
    public void testObjectNotInIndex() throws Exception {
        insertAndAdd(points1);
        geogit.command(CommitOp.class).setMessage("Commit1").call();

        Optional<RevFeatureType> featureType = geogit.command(ResolveFeatureType.class)
                .setFeatureType("WORK_HEAD:" + linesName).call();
        assertFalse(featureType.isPresent());
    }
}
