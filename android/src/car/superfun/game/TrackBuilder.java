package car.superfun.game;

import com.badlogic.gdx.graphics.Texture;
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

import java.util.ArrayList;

import car.superfun.game.gameModes.GladiatorMode;
import car.superfun.game.gameModes.PlayState;


public class TrackBuilder {

    static float pixelsPerTile;

    public static Array<Body> buildGoalLine(Map map, float pixels, World world) {

        MapObjects mapObjects;
        Array<Body> bodies;
        Shape shape;
        BodyDef bodyDef;
        Body body;
        FixtureDef fixtureDef;

        pixelsPerTile = pixels;
        mapObjects = map.getLayers().get("goal_line").getObjects();
        bodies = new Array<Body>();

        for (MapObject object : mapObjects) {
            if (object instanceof TextureMapObject) {
                shape = getRectangle((RectangleMapObject) object);
            }
            else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject)object);
            }
            else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject)object);
            }
            else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject)object);
            }
            else {
                continue;
            }

            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            fixtureDef = new FixtureDef();
            fixtureDef.filter.categoryBits = PlayState.GOAL_ENTITY;
            fixtureDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;
            fixtureDef.shape = shape;
            fixtureDef.isSensor = true;

            body = world.createBody(bodyDef);
            body.createFixture(fixtureDef).setDensity(0.5f);
            bodies.add(body);
            shape.dispose();
        }
        return bodies;
    }

    public static Array<Body> buildWalls(Map map, float pixels, World world) {

        MapObjects mapObjects;
        Array<Body> bodies;
        Shape shape;
        BodyDef bodyDef;
        Body body;
        FixtureDef fixtureDef;

        pixelsPerTile = pixels;
        mapObjects = map.getLayers().get("walls").getObjects();
        bodies = new Array<Body>();

        for (MapObject object : mapObjects) {
            if (object instanceof TextureMapObject) {
                shape = getRectangle((RectangleMapObject) object);
            }
            else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject)object);
            }
            else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject)object);
            }
            else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject)object);
            }
            else {
                continue;
            }

            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            fixtureDef = new FixtureDef();
            fixtureDef.filter.categoryBits = GlobalVariables.WALL_ENTITY;
            fixtureDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;
            fixtureDef.shape = shape;

            body = world.createBody(bodyDef);
            body.createFixture(fixtureDef).setDensity(0.9f);
            bodies.add(body);
            shape.dispose();
        }
        return bodies;
    }

    public static Array<Body> buildDeathZone(Map map, float pixels, World world) {

        MapObjects mapObjects;
        Array<Body> bodies;
        Shape shape;
        BodyDef bodyDef;
        Body body;
        FixtureDef fixtureDef;

        pixelsPerTile = pixels;
        mapObjects = map.getLayers().get("dirt_barrier").getObjects();
        bodies = new Array<Body>();

        for (MapObject object : mapObjects) {
            if (object instanceof TextureMapObject) {
                shape = getRectangle((RectangleMapObject) object);
            }
            else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject)object);
            }
            else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject)object);
            }
            else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject)object);
            }
            else {
                continue;
            }

            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            fixtureDef = new FixtureDef();
            fixtureDef.filter.categoryBits = GladiatorMode.DEATH_ENTITY;
            fixtureDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;
            fixtureDef.shape = shape;

            body = world.createBody(bodyDef);
            body.createFixture(fixtureDef);
            bodies.add(body);
            shape.dispose();
        }
        return bodies;
    }

    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle;
        PolygonShape polygonShape;
        Vector2 size;

        rectangle = rectangleObject.getRectangle();
        polygonShape = new PolygonShape();
        size = new Vector2((rectangle.x + rectangle.width * 0.5f) / pixelsPerTile,(rectangle.y + rectangle.height * 0.5f ) / pixelsPerTile);
        polygonShape.setAsBox(rectangle.width * 0.5f / pixelsPerTile, rectangle.height * 0.5f / pixelsPerTile, size, 0.0f);
        return polygonShape;
    }

    private static CircleShape getCircle(CircleMapObject circleMapObject) {
        Circle circle;
        CircleShape circleShape;

        circle = circleMapObject.getCircle();
        circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / pixelsPerTile);
        circleShape.setPosition(new Vector2(circle.x / pixelsPerTile, circle.y / pixelsPerTile));
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
            worldVertices[i] = vertices[i] / pixelsPerTile;
        }

        polygonShape.set(worldVertices);
        return polygonShape;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineMapObject) {
        float[] vertices = polylineMapObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / pixelsPerTile;
            worldVertices[i].y = vertices[i * 2 + 1] / pixelsPerTile;
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);
        return chainShape;
    }

}