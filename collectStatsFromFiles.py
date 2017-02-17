import os, sys, json
import fetch, fetch_corpus

# Open and parse corpus.json
WORKING_DIR = os.path.dirname(os.path.realpath(__file__))
CORPUS_INFO = None
with open(os.path.join(WORKING_DIR, 'corpus.json')) as f:
	CORPUS_INFO = json.loads(f.read())
CORPUS_DIR = os.path.join(WORKING_DIR, "corpus")

def collect_statistics():
	ontology = os.getcwd()
	with fetch_corpus.cd(CORPUS_DIR):
		for project in CORPUS_INFO['projects'].values():
			if os.path.isdir(project['name']):
				PROJECT_DIR = os.path.join(CORPUS_DIR, project['name'])
				if 'build-dir' in project.keys():
					PROJECT_DIR = os.path.join(PROJECT_DIR, project['build-dir'])

				with fetch_corpus.cd(PROJECT_DIR):
					print 'Running ' + project['name']
					print fetch_corpus.run_cmd([ 'bash', WORKING_DIR+'/countAnnotationsFromJaif.sh', PROJECT_DIR ])['output']

					"""
					print "annotations in java file: "
					print fetch_corpus.run_cmd('find' + PROJECT_DIR + '-name *.java | xargs grep -l "@Ontology(" | grep "annotated" | xargs grep -o "@Ontology(" | wc -l')['output']

					print "ontology values in java file: "
					print fetch_corpus.run_cmd('find' + PROJECT_DIR + '-name *.java | xargs grep -l "@Ontology(" | grep "annotated" | xargs grep -o "OntologyValue.ontology" | wc -l')['output']
					
					print "in jaif file: "
					print fetch_corpus.run_cmd('find' + PROJECT_DIR + '-name default.jaif | xargs grep -o "@ontology.qual" | wc -l')['output']
					
					print "Files, blank, comment, source LOC total:"
					print fetch_corpus.run_cmd('cloc' + PROJECT_DIR + '| grep Java')['output']
					
					print "Files, blank, comment, source LOC in annotated:"
					print fetch_corpus.run_cmd('cloc' + os.path.join(PROJECT_DIR, 'annotated') + '| grep Java')['output']
					"""

if __name__ == "__main__":
	collect_statistics()