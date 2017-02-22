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
			if os.path.isdir(project['name']) and project['name'] in ['java-callgraph', 'logback-extensions', 'cal10n']:
				PROJECT_DIR = os.path.join(CORPUS_DIR, project['name'])
				if 'build-dir' in project.keys():
					PROJECT_DIR = os.path.join(PROJECT_DIR, project['build-dir'])

				with fetch_corpus.cd(PROJECT_DIR):
					print 'Running ' + project['name']
					print fetch_corpus.run_cmd([ 'bash', WORKING_DIR+'/run-dljc-stats.sh', project['build'] ])['output']

if __name__ == "__main__":
	collect_statistics()