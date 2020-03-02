package es.idea.xes;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import PetriNet.PetriNet;
import PetriNet.TransitionPetriNet;
import PetriNet.LPO.PetriNetLpo;
import es.idea.pnml.Pnml.Net.Transition;
import test.EscribeFichero;
import test.SimplePetriNetEjemplosTest;
import java.io.InputStream;

public class XesUtils {

	public static XLog getXLogFromStream(InputStream xes) throws Exception{
		XesXmlParser xparser = new XesXmlParser();
		return xparser.parse(xes).get(0);
	}

	public static List<String> getRawLog(XTrace xtrace){
        List<String> logs_xes = new ArrayList<>();

        for (XEvent x : xtrace) {
            logs_xes.add(x.getAttributes().get("concept:name").toString());
        }
        return logs_xes;
	}

	public static XLog getXLog(String xesPath) throws Exception {
		File xesFile = new File(XesUtils.class.getResource(xesPath).getPath());

		XesXmlParser xparser = new XesXmlParser();

		List<XLog> xLogList = xparser.parse(xesFile);

		return xLogList.get(0);

	}
	
	public static XTrace getXTrace(XLog xlog, int index) {
		return xlog.get(index);

	}

	public static List<String> getLog(XTrace xtrace, PetriNetLpo net){
		List<String> logs_xesSinTraducir = new ArrayList<>();
		List<String> logs_xes = new ArrayList<>();

		for (XEvent x : xtrace) {
			logs_xesSinTraducir.add(x.getAttributes().get("concept:name").toString());

			List<Transition> t = net.findTransitionByName(x.getAttributes().get("concept:name").toString());

			if (!t.isEmpty()) {
				if (t.size() == 1)
					logs_xes.add(t.get(0).getName().getText());
				else
					logs_xes.add(t.stream().findFirst().get().getName().getText());
			} else {
				logs_xes.add(x.getAttributes().get("concept:name").toString() + "-NotInModel");
			}
		}

		return logs_xes;
		
	}

	public static List<String> xesLogExtended(String xesPath, Integer index, PetriNetLpo net) throws Exception {

		File xesFile = new File(XesUtils.class.getResource(xesPath).getPath());

		XesXmlParser xparser = new XesXmlParser();

		List<XLog> xLogList = xparser.parse(xesFile);

		XLog b = xLogList.get(0);

		XTrace oneTrace = b.get(index);

		List<String> logs_xesSinTraducir = new ArrayList<>();
		List<String> logs_xes = new ArrayList<>();

		for (XEvent x : oneTrace) {
			logs_xesSinTraducir.add(x.getAttributes().get("concept:name").toString());

			List<Transition> t = net.findTransitionByName(x.getAttributes().get("concept:name").toString());

			if (!t.isEmpty()) {
				if (t.size() == 1)
					logs_xes.add(t.get(0).getName().getText());
				else
					logs_xes.add(t.stream().findFirst().get().getName().getText());
			} else {
				logs_xes.add(x.getAttributes().get("concept:name").toString() + "-NotInModel");
			}
		}

		return logs_xes;
	}

	public static List<List<String>> xesLogExtendedV2(String xesPath, PetriNetLpo net) throws Exception {

		File xesFile = new File(XesUtils.class.getResource(xesPath).getPath());

		XesXmlParser xparser = new XesXmlParser();

		List<XLog> xLogList = xparser.parse(xesFile);

		XLog b = xLogList.get(0);

		List<List<String>> res = new ArrayList<List<String>>();
		List<String> logs_xesSinTraducir = new ArrayList<>();
		List<String> logs_xes = new ArrayList<>();

		for (XTrace trace : b) {
			for (XEvent x : trace) {
				logs_xesSinTraducir.add(x.getAttributes().get("concept:name").toString());

				List<Transition> t = net.findTransitionByName(x.getAttributes().get("concept:name").toString());

				if (!t.isEmpty()) {
					if (t.size() == 1)
						logs_xes.add(t.get(0).getName().getText());
					// logs_xes.add(t.get(0).getId());
					else
						logs_xes.add(t.stream().findFirst().get().getName().getText());
					// logs_xes.add(t.stream().findFirst().get().getId());
				} else {
					logs_xes.add(x.getAttributes().get("concept:name").toString() + "-NotInModel");
				}
			}

			res.add(new ArrayList<>(logs_xes));
			logs_xes.clear();
		}

		// String resultLog = "Logs Sin traducir:" + logs_xesSinTraducir + "\n";
		// resultLog = resultLog + "Logs traducido: " + logs_xes + "\n";
		// EscribeFichero.execute(resultLog, fichero);

		return res;
	}

	public static List<String> xesLog(String xesPath, Integer index, PetriNet net, String add, FileWriter fichero)
			throws Exception {
		// String xesFilePath="/xes/2000-all-noises.xes";
		// String xesFileAbsolute =
		// "C:/Users/David/workspace/conformanceCheking/ConfCheck/src/main/resources/xes/2000-all-noise.xes";

		File xesFile = new File(XesUtils.class.getResource(xesPath).getPath());

		XesXmlParser xparser = new XesXmlParser();

		// boolean a = xparser.canParse(xesFile);

		// System.out.println(a);

		List<XLog> xLogList = xparser.parse(xesFile);
		//
		XLog b = xLogList.get(0);
		// System.out.println(b.size());

		// CAMBIAR PARA VARIAR TRAZA
		XTrace oneTrace = b.get(index - 1);

		// XEvent oneEvent=oneTrace.get(0);

		List<String> logs_xesSinTraducir = new ArrayList<>();
		List<String> logs_xes = new ArrayList<>();

		List<TransitionPetriNet> transitions = net.constructTransitions();
		// List<PlacePetriNet> places = net.constructPlaces();

		//
		for (XEvent x : oneTrace) {
			logs_xesSinTraducir.add(x.getAttributes().get("concept:name").toString());

			logs_xes.add(net.nameToId2(x.getAttributes().get("concept:name").toString(), transitions, add));
		}

		String resultLog = "Logs Sin traducir:" + logs_xesSinTraducir + "\n";
		resultLog = resultLog + "Logs traducido" + logs_xes + "\n";
		EscribeFichero.execute(resultLog, fichero);

		return logs_xes;
	}

	public static List<String> xesLog(String xesPath, PetriNet net, Integer index, FileWriter fichero)
			throws Exception {
		// String xesFilePath="/xes/2000-all-noises.xes";
		// String xesFileAbsolute =
		// "C:/Users/David/workspace/conformanceCheking/ConfCheck/src/main/resources/xes/2000-all-noise.xes";

		File xesFile = new File(SimplePetriNetEjemplosTest.class.getResource(xesPath).getFile());
		XesXmlParser xparser = new XesXmlParser();

		// boolean a = xparser.canParse(xesFile);
		// System.out.println(a);

		List<XLog> xLogList = xparser.parse(xesFile);
		//
		XLog b = xLogList.get(0);
		// System.out.println(b.size());

		// CAMBIAR PARA VARIAR TRAZA
		XTrace oneTrace = b.get(index);

		// XEvent oneEvent=oneTrace.get(0);

		List<String> logs_xesSinTraducir = new ArrayList<>();
		List<String> logs_xes = new ArrayList<>();

		List<TransitionPetriNet> transitions = net.constructTransitions();
		// List<PlacePetriNet> places = net.constructPlaces();

		for (XEvent x : oneTrace) {
			logs_xesSinTraducir.add(x.getAttributes().get("concept:name").toString());

			logs_xes.add(net.nameToId(x.getAttributes().get("concept:name").toString(), transitions));
		}

		String resultLog = "Logs Sin traducir:" + logs_xesSinTraducir + "\n";
		resultLog = resultLog + "Logs traducido" + logs_xes + "\n";
		EscribeFichero.execute(resultLog, fichero);

		return logs_xes;
	}

	public static Integer size(String xesPath) throws Exception {

		File xesFile = new File(XesUtils.class.getResource(xesPath).getPath());

		XesXmlParser xparser = new XesXmlParser();

		return xparser.parse(xesFile).get(0).size();

	}
	
}
