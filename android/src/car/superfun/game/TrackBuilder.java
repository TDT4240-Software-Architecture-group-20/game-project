package car.superfun.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;


public class TrackBuilder {

    public static Array<Body> buildLayer(Map map, World world, String layerName, FixtureDef fixtureDef) {
        MapObjects mapObjects = map.getLayers().get(layerName).getObjects();
        Array<Body> bodies = new Array<>();

        for (MapObject mapObject : mapObjects) {
            Shape shape;
            try {
                shape = getShape(mapObject);
            } catch (ClassNotFoundException ex) {
                Gdx.app.error("Can't find class for mapObject", mapObject.toString());
                return bodies;
            }

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            fixtureDef.shape = shape;

            Body body = world.createBody(bodyDef);
            body.createFixture(fixtureDef);
            bodies.add(body);
            shape.dispose();
        }

        return bodies;
    }

    public static Array<Body> buildLayerWithUserData(Map map, World world, String layerName, FixtureDef fixtureDef, UserDataCreater userDataCreater) {
        MapObjects mapObjects = map.getLayers().get(layerName).getObjects();
        Array<Body> bodies = new Array<>();

        for (MapObject mapObject : mapObjects) {
            Shape shape;
            try {
                shape = getShape(mapObject);
            } catch (ClassNotFoundException ex) {
                Gdx.app.error("Can't find class for mapObject", mapObject.toString());
                return bodies;
            }

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            fixtureDef.shape = shape;

            Body body = world.createBody(bodyDef);
            body.createFixture(fixtureDef).setUserData(userDataCreater.getUserData());
            bodies.add(body);
            shape.dispose();
        }

        return bodies;
    }

    private static Shape getShape(MapObject mapObject) throws ClassNotFoundException {
        Shape shape;
        if (mapObject instanceof TextureMapObject) {
            shape = getRectangle((RectangleMapObject) mapObject);
        }
        else if (mapObject instanceof PolygonMapObject) {
            shape = getPolygon((PolygonMapObject)mapObject);
        }
        else if (mapObject instanceof PolylineMapObject) {
            shape = getPolyline((PolylineMapObject)mapObject);
        }
        else if (mapObject instanceof CircleMapObject) {
            shape = getCircle((CircleMapObject)mapObject);
        } else {
            throw new ClassNotFoundException("Cannot find class for mapObject");
        }
        return shape;
    }

    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle;
        PolygonShape polygonShape;
        Vector2 size;

        rectangle = rectangleObject.getRectangle();
        polygonShape = new PolygonShape();
        size = new Vector2((rectangle.x + rectangle.width * 0.5f) / CarSuperFun.PIXELS_TO_METERS,(rectangle.y + rectangle.height * 0.5f ) / CarSuperFun.PIXELS_TO_METERS);
        polygonShape.setAsBox(rectangle.width * 0.5f / CarSuperFun.PIXELS_TO_METERS, rectangle.height * 0.5f / CarSuperFun.PIXELS_TO_METERS, size, 0.0f);
        return polygonShape;
    }

    private static CircleShape getCircle(CircleMapObject circleMapObject) {
        Circle circle;
        CircleShape circleShape;

        circle = circleMapObject.getCircle();
        circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / CarSuperFun.PIXELS_TO_METERS);
        circleShape.setPosition(new Vector2(circle.x / CarSuperFun.PIXELS_TO_METERS, circle.y / CarSuperFun.PIXELS_TO_METERS));
        return circleShape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonMapObject) {
        PolygonShape polygonShape;
        float[] vertices;
        float[] worldVertices;

        polygonShape = new PolygonShape();
        vertices = polygonMapObject.getPolygon().getTransformedVertices();
        worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i] / CarSuperFun.PIXELS_TO_METERS;
        }

        polygonShape.set(worldVertices);
        return polygonShape;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineMapObject) {
        float[] vertices = polylineMapObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / CarSuperFun.PIXELS_TO_METERS;
            worldVertices[i].y = vertices[i * 2 + 1] / CarSuperFun.PIXELS_TO_METERS;
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);
        return chainShape;
    }

}