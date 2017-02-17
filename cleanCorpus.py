import os, sys, json
import fetch, fetch_corpus

# Open and parse corpus.json
WORKING_DIR = os.path.dirname(os.path.realpath(__file__))
CORPUS_INFO = None
with open(os.path.join(WORKING_DIR, 'corpus.json')) as f:
	CORPUS_INFO = json.loads(f.read())
CORPUS_DIR = os.path.join(WORKING_DIR, "corpus")

def clean_corpus():
	ontology = os.getcwd()
	with fetch_corpus.cd(CORPUS_DIR):
		for project in CORPUS_INFO['projects'].values():
			if os.path.isdir(project['name']):
				PROJECT_DIR = os.path.join(CORPUS_DIR, project['name'])
				if 'build-dir' in project.keys():
					PROJECT_DIR = os.path.join(PROJECT_DIR, project['build-dir'])

				with fetch_corpus.cd(PROJECT_DIR):
					print 'Cleaning ' + project['name']
					print fetch_corpus.run_cmd(project['clean'].split(' '))['output']
					print fetch_corpus.run_cmd(['rm', '-rf', 'ontology.log', 'ontology-error.log', 'ontology-inferred-slots-statistic.txt', 'post-verification-statistic.txt', 'solver-statistic.txt', 'annotated', 'default.jaif', 'logs'])['output']

if __name__ == "__main__":
	clean_corpus()