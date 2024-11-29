package ma.ensa.graphqltp.network

import com.apollographql.apollo3.ApolloClient


object ApolloClientProvider {
    fun getApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("http://10.0.2.2:8082/graphql")
            .build()
    }
}
