package com.brownietech.emtbus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.glob3.mobile.generated.AltitudeMode;
import org.glob3.mobile.generated.Angle;
import org.glob3.mobile.generated.G3MContext;
import org.glob3.mobile.generated.GEO2DLineStringGeometry;
import org.glob3.mobile.generated.GEO2DMultiLineStringGeometry;
import org.glob3.mobile.generated.GEO2DMultiPolygonGeometry;
import org.glob3.mobile.generated.GEO2DPointGeometry;
import org.glob3.mobile.generated.GEO2DPolygonGeometry;
import org.glob3.mobile.generated.GEO3DPointGeometry;
import org.glob3.mobile.generated.GEO3DPolygonGeometry;
import org.glob3.mobile.generated.GEOMarkSymbol;
import org.glob3.mobile.generated.GEORenderer;
import org.glob3.mobile.generated.GEOSymbol;
import org.glob3.mobile.generated.GEOSymbolizer;
import org.glob3.mobile.generated.GInitializationTask;
import org.glob3.mobile.generated.GTask;
import org.glob3.mobile.generated.Geodetic2D;
import org.glob3.mobile.generated.Geodetic3D;
import org.glob3.mobile.generated.IThreadUtils;
import org.glob3.mobile.generated.JSONObject;
import org.glob3.mobile.generated.LayerSet;
import org.glob3.mobile.generated.Mark;
import org.glob3.mobile.generated.MarksRenderer;
import org.glob3.mobile.generated.Sector;
import org.glob3.mobile.generated.URL;
import org.glob3.mobile.generated.WMSLayer;
import org.glob3.mobile.generated.WMSServerVersion;
import org.glob3.mobile.specific.G3MBuilder_Android;
import org.glob3.mobile.specific.G3MWidget_Android;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private Location mLocation;
    MarksRenderer _positionRenderer = new MarksRenderer(false);
    MarksRenderer _busStopsRenderer = new MarksRenderer(false);
    private G3MWidget_Android _g3mWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dexter.withActivity(MainActivity.this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            startGlob3();
                        } else {
                            Toast.makeText(MainActivity.this, getText(R.string.ask_for_location), Toast.LENGTH_LONG).show();
                            startGlob3();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        findViewById(R.id.gotolocation).bringToFront();
        findViewById(R.id.gotolocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IThreadUtils tu = _g3mWidget.getG3MContext().getThreadUtils();
                tu.invokeInRendererThread(new GTask() {
                    @Override
                    public void run(G3MContext context) {
                        Location location = LocationUtil.getLocation(MainActivity.this);
                        LocationUtil.setLocationFromGPS("file:///position.png",_positionRenderer, location,_g3mWidget.getG3MWidget(),0);
                    }
                },true);

            }
        });
    }

    private void startGlob3() {
        ConstraintLayout cl = findViewById(R.id.g3m);
        final G3MBuilder_Android builder = new G3MBuilder_Android(this);
        builder.setAtmosphere(true);
        final Sector madSector = Sector.fromDegrees(40.2987, -3.882, 40.5422, -3.49);



        builder.setInitializationTask(new GInitializationTask() {
            @Override
            public boolean isDone(G3MContext context) {
                 return true;
            }

            @Override
            public void run(G3MContext context) {


                Location location = LocationUtil.getLocation(MainActivity.this);


                if(madSector.contains(new Geodetic2D(Angle.fromDegrees(location.getLatitude()),Angle.fromDegrees(location.getLongitude())))){
                    LocationUtil.setLocationFromGPS("file:///position.png",_positionRenderer, location,_g3mWidget.getG3MWidget(),0);
                }else{
                    LocationUtil.setLocationFromGPS("file:///position.png",_positionRenderer, null,_g3mWidget.getG3MWidget(),5000);
                }

            }
        });


        LayerSet layerSet = new LayerSet();

        WMSLayer pnoa = WMSLayer.newWGS84("OI.OrthoimageCoverage" ,
                new URL("http://www.ign.es/wms-inspire/pnoa-ma?"),
                WMSServerVersion.WMS_1_3_0,
                Sector.FULL_SPHERE,
                "image%2Fjpeg",
                "",
                false
                );

        WMSLayer ignCallejero = WMSLayer.newWGS84("IGNBaseOrto" ,
                new URL("http://www.ign.es/wms-inspire/ign-base?"),
                WMSServerVersion.WMS_1_3_0,
                Sector.FULL_SPHERE,
                "image%2Fpng",
                "default",
                true
        );


        layerSet.addLayer(pnoa);
        layerSet.addLayer(ignCallejero);
        builder.getPlanetRendererBuilder().setLayerSet(layerSet);
        builder.addRenderer(_positionRenderer);
        builder.addRenderer(_busStopsRenderer);

        GEORenderer vectorLayerRenderer = builder.createGEORenderer(Symbolizer,true,true,true,false);
        vectorLayerRenderer.loadJSON(new URL("file:///bus-stop.geojson"));


        _g3mWidget = builder.createWidget();
        _g3mWidget.getG3MWidget().setRenderedSector(madSector);
        cl.addView(_g3mWidget);
    }

    public GEOSymbolizer Symbolizer = new GEOSymbolizer() {

        @Override
        public ArrayList<GEOSymbol> createSymbols(GEO2DPointGeometry geometry) {

            final ArrayList<GEOSymbol> result = new ArrayList<GEOSymbol>();
            final JSONObject properties = geometry.getFeature().getProperties();

            final String busStop = properties.getAsString("CODIGOEMPR", "");
            final String denominacion = properties.getAsString("DENOMINACI", "");

            BusStopTouchListener vmtl = new BusStopTouchListener(busStop,denominacion,MainActivity.this.getSupportFragmentManager(),MainActivity.this);

            final Mark mark = new Mark(

                    new URL("file:///bus-stop2.png", false),  //
                    //new URL(Constants._iconsPath+"/"+_icon, false),  //
                    // new Geodetic3D(geometry.getPosition(), 40),
                    new Geodetic3D(geometry.getPosition(), 0),
                    AltitudeMode.RELATIVE_TO_GROUND,
                    0,
                    null,
                    false,
                    vmtl,
                    true
            );
//
//            final Mark mark2 = new Mark(
//                    denominacion,
//                    new URL("file:///bus-stop2.png", false),
//                    new Geodetic3D(geometry.getPosition(), 0),
//                    AltitudeMode.RELATIVE_TO_GROUND,
//                    0,
//                    true,
//                    9,
//                    Color.WHITE,
//                    Color.BLACK,
//                    1,
//                    null,
//                    true,
//                    vmtl,
//                    true
//                    );

            result.add(new GEOMarkSymbol(mark));
            return result;
        }

        @Override
        public ArrayList<GEOSymbol> createSymbols(GEO3DPointGeometry geometry) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ArrayList<GEOSymbol> createSymbols(GEO2DLineStringGeometry geometry) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ArrayList<GEOSymbol> createSymbols(GEO2DMultiLineStringGeometry geometry) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ArrayList<GEOSymbol> createSymbols(GEO2DPolygonGeometry geometry) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ArrayList<GEOSymbol> createSymbols(GEO3DPolygonGeometry geometry) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ArrayList<GEOSymbol> createSymbols(GEO2DMultiPolygonGeometry geometry) {
            // TODO Auto-generated method stub
            return null;
        }

    };


}
