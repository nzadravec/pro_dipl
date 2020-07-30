package hr.fer.zemris.gthpw;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;

import hr.fer.zemris.gthpw.actions.AutoAnnotateCharsLabelAction;
import hr.fer.zemris.gthpw.actions.AutoAnnotateSegmentsAction;
import hr.fer.zemris.gthpw.actions.AutoAnnotateTextLinesAction;
import hr.fer.zemris.gthpw.actions.AutoAnnotateWordsAction;
import hr.fer.zemris.gthpw.actions.ExitAction;
import hr.fer.zemris.gthpw.actions.OpenDocImageAction;
import hr.fer.zemris.gthpw.actions.LoadGroundTruthAction;
import hr.fer.zemris.gthpw.actions.ReloadTranscriptAction;
import hr.fer.zemris.gthpw.actions.SaveAction;
import hr.fer.zemris.gthpw.actions.ShowAction;
import hr.fer.zemris.gthpw.annotating_tools.ActivatedAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.AnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.IdleAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.segment.AddingSegmentAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.segment.MergingSegmentsAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.segment.RemovingSegmentsAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.segment.ReshapingSegmentAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.segment.SegmentsAutoAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.segment.SplittingSegmentAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.textline.AddingTextLineAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.textline.LinkingTextLinesAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.textline.TextLinesAutoAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.word.WordAnnotatingTool;
import hr.fer.zemris.gthpw.annotating_tools.word.WordsAutoAnnotatingTool;

public class JGTHPW extends JFrame {

	private static final long serialVersionUID = 1L;

	private ExitAction exitAction;

	public JGTHPW() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exitAction.actionPerformed(null);
			}
		});

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize);
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		setTitle("Ground-Truthing Hand-Printed Words");
		initGUI();
	}

	private void initGUI() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		ActivatedAnnotatingTool activatedAnnotatingTool = new ActivatedAnnotatingTool();
		JAnnotatingDocImage annotatingDocImage = new JAnnotatingDocImage(activatedAnnotatingTool);

		AnnotatingDocImageModel annotatingDocImageModel = annotatingDocImage.getModel();
		createMenus(annotatingDocImageModel);
		createTools(annotatingDocImageModel, activatedAnnotatingTool);

		add(new JScrollPane(annotatingDocImage));
	}

	private void createMenus(AnnotatingDocImageModel model) {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		exitAction = new ExitAction("Exit", this, model);
		fileMenu.add(new OpenDocImageAction("Open doc image", this, model, exitAction));
		fileMenu.add(new ReloadTranscriptAction("Reload transcript", this, model));
		fileMenu.add(new LoadGroundTruthAction("Load ground truth", this, model));
		fileMenu.add(new SaveAction("Save ground truth", this, model, exitAction));
		fileMenu.add(exitAction);
	}

	private void createTools(AnnotatingDocImageModel model, ActivatedAnnotatingTool currentTool) {
		currentTool.setAnnotatingTool(new IdleAnnotatingTool(null, currentTool, model));
		
		JPanel toolbarsPanel = new JPanel(new GridLayout(-1, 1));
		getContentPane().add(toolbarsPanel, BorderLayout.PAGE_START);
		
		JToolBar toolBar1 = new JToolBar();
		toolbarsPanel.add(toolBar1);

		toolBar1.add(new JLabel("Auto annotate: "));
		toolBar1.add(new JButton(new AutoAnnotateSegmentsAction("CCs", model)));
		toolBar1.add(new JButton(new AutoAnnotateTextLinesAction("Lines", model)));
		toolBar1.add(new JButton(new AutoAnnotateWordsAction("Words", model)));
		toolBar1.add(new JButton(new AutoAnnotateCharsLabelAction("Chars Label", model)));
		
		toolBar1.addSeparator();

		toolBar1.add(new JLabel("Show: "));
		JToggleButton showSegmentsButton = new JToggleButton(
				new ShowAction("Chars", model, value -> model.showSegments(value)));
		toolBar1.add(showSegmentsButton);

		JToggleButton showTextLinesButton = new JToggleButton(
				new ShowAction("Lines", model, value -> model.showTextLines(value)));
		toolBar1.add(showTextLinesButton);

		JToggleButton showWordsButton = new JToggleButton(new ShowAction("Words", model, value -> {
			model.showWords(value);
		}));
		toolBar1.add(showWordsButton);
		
		JToggleButton showCharsLabelButton = new JToggleButton(new ShowAction("Chars label", model, value -> {
			model.showCharsLabel(value);
		}));
		toolBar1.add(showCharsLabelButton);
		
		showSegmentsButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					showCharsLabelButton.setEnabled(true);
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					showCharsLabelButton.setEnabled(false);
				}
			}
		});

		JToggleButton showWordsLabelButton = new JToggleButton(
				new ShowAction("Transcript words", model, value -> model.showTranscriptWords(value)));
		toolBar1.add(showWordsLabelButton);

		showWordsButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					showWordsLabelButton.setEnabled(true);
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					showWordsLabelButton.setEnabled(false);
				}
			}
		});

		toolBar1.addSeparator();
		
		
		JToolBar toolBar2 = new JToolBar();
		toolbarsPanel.add(toolBar2);

		List<AnnotatingTool> annotatingSegmentsTools = new ArrayList<>();
		List<ButtonModel> annotatingSegmentsButtonModels = new ArrayList<>();

		annotatingSegmentsTools.add(new MergingSegmentsAnnotatingTool("merge", currentTool, model));
		annotatingSegmentsTools.add(new RemovingSegmentsAnnotatingTool("delete", currentTool, model));
		annotatingSegmentsTools.add(new AddingSegmentAnnotatingTool("add", currentTool, model));
		annotatingSegmentsTools.add(new SplittingSegmentAnnotatingTool("split", currentTool, model));
		annotatingSegmentsTools.add(new ReshapingSegmentAnnotatingTool("reshape", currentTool, model));
		annotatingSegmentsTools.add(new SegmentsAutoAnnotatingTool("auto", currentTool, model));

		ButtonGroup annotatingToolGroup = new ButtonGroup();

		toolBar2.add(new JLabel("Char annotate: "));
		for (AnnotatingTool tool : annotatingSegmentsTools) {
			JToggleButton button = new JToggleButton(tool);
			annotatingToolGroup.add(button);
			toolBar2.add(button);
			annotatingSegmentsButtonModels.add(button.getModel());
		}

		toolBar2.addSeparator();

		List<AnnotatingTool> annotatingTextLinesTools = new ArrayList<>();
		List<ButtonModel> annotatingTextLinesButtonModels = new ArrayList<>();

		annotatingTextLinesTools.add(new AddingTextLineAnnotatingTool("add", currentTool, model));
		annotatingTextLinesTools.add(new LinkingTextLinesAnnotatingTool("link", currentTool, model));
		annotatingTextLinesTools.add(new TextLinesAutoAnnotatingTool("auto", currentTool, model));

		toolBar2.add(new JLabel("Line annotate: "));
		for (AnnotatingTool tool : annotatingTextLinesTools) {
			JToggleButton button = new JToggleButton(tool);
			annotatingToolGroup.add(button);
			toolBar2.add(button);
			annotatingTextLinesButtonModels.add(button.getModel());
		}

		toolBar2.addSeparator();

		List<AnnotatingTool> annotatingWordsTools = new ArrayList<>();
		List<ButtonModel> annotatingWordsButtonModels = new ArrayList<>();

		annotatingWordsTools.add(new WordAnnotatingTool("add", currentTool, model));
		annotatingWordsTools.add(new WordsAutoAnnotatingTool("auto", currentTool, model));

		toolBar2.add(new JLabel("Word annotate: "));
		for (AnnotatingTool tool : annotatingWordsTools) {
			JToggleButton button = new JToggleButton(tool);
			annotatingToolGroup.add(button);
			toolBar2.add(button);
			annotatingWordsButtonModels.add(button.getModel());
		}

		model.addListener(new AnnotatingDocImageModelAdapter() {
			@Override
			public void stateChanged(ChangeEvent e) {
				showSegmentsButton.setSelected(model.showSegments());
				showTextLinesButton.setSelected(model.showTextLines());
				showWordsButton.setSelected(model.showWords());
				showCharsLabelButton.setSelected(model.showCharsLabel());
				showWordsLabelButton.setSelected(model.showTranscriptWords());

				if (!model.showSegments()) {
					for (ButtonModel m : annotatingSegmentsButtonModels) {
						if (annotatingToolGroup.isSelected(m)) {
							annotatingToolGroup.clearSelection();
							currentTool.setAnnotatingTool(new IdleAnnotatingTool(null, currentTool, model));
						}
					}
				}

				if (!model.showTextLines()) {
					for (ButtonModel m : annotatingTextLinesButtonModels) {
						if (annotatingToolGroup.isSelected(m)) {
							annotatingToolGroup.clearSelection();
							currentTool.setAnnotatingTool(new IdleAnnotatingTool(null, currentTool, model));
						}
					}
				}

				if (!model.showWords()) {
					for (ButtonModel m : annotatingWordsButtonModels) {
						if (annotatingToolGroup.isSelected(m)) {
							annotatingToolGroup.clearSelection();
							currentTool.setAnnotatingTool(new IdleAnnotatingTool(null, currentTool, model));
						}
					}
				}
			}
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new JGTHPW().setVisible(true));
	}

}
