<template>
    <v-card class="container background">
        <v-card-title>Импорт списка</v-card-title>
        <v-card-text>
            <div class="large-12 medium-12 small-12 cell">
                <label>
                    <input type="file" id="file" ref="file" v-on:change="handleFileUpload()"/>
                </label>
                <v-btn v-on:click="submitFile()">Submit</v-btn>
            </div>
        </v-card-text>
    </v-card>
</template>

<script>
import { $http } from '@/service/http';

export default {
    name: 'ImportForm',
    data() {
        return {
            file: ''
        };
    }, methods: {
        submitFile() {
            let formData = new FormData();
            formData.append('file', this.file);
            $http.post('application/excel/import',
                formData,
                {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                }
            ).then((response) => {
                let list = response.data;
                let issuedApplications = [];
                let acceptedApplications = [];

                list.forEach(application => {
                    if (application.status === 'ISSUED') {
                        issuedApplications.push(application);
                    } else {
                        acceptedApplications.push(application);
                    }
                });

                this.$bus.$emit('issued', issuedApplications);
                this.$bus.$emit('accepted', acceptedApplications);

                this.$emit('close');
            }).catch(() => {
                    console.log('FAILURE!!');
                    this.$emit('submit');
                }
            );
        },

        handleFileUpload() {
            this.file = this.$refs.file.files[0];
        }
    }
};
</script>

<style scoped>
.background {
    background: white;
}
</style>