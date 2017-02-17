import os, sys, json
import fetch, fetch_corpus

# Open and parse corpus.json
WORKING_DIR = os.path.dirname(os.path.realpath(__file__))
CORPUS_INFO = None
with open(os.path.join(WORKING_DIR, 'corpus.json')) as f:
	CORPUS_INFO = json.loads(f.read())
CORPUS_DIR = os.path.join(WORKING_DIR, "corpus")

def build_corpus_no_inference():
	ontology = os.getcwd()
	with fetch_corpus.cd(CORPUS_DIR):
		for project in CORPUS_INFO['projects'].values():
			if os.path.isdir(project['name']):
				PROJECT_DIR = os.path.join(CORPUS_DIR, project['name'])
				if 'build-dir' in project.keys():
					PROJECT_DIR = os.path.join(PROJECT_DIR, project['build-dir'])

				with fetch_corpus.cd(PROJECT_DIR):
					print 'Compiling w/o inference ' + project['name']
					print fetch_corpus.run_cmd(project['build'].split(' '))['output']

if __name__ == "__main__":
	build_corpus_no_inference()